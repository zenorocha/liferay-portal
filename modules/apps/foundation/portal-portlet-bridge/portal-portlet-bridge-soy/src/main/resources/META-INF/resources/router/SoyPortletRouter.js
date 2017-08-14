import Alert from 'metal-alert/src/Alert';
import async from 'metal/src/async/async';
import Component from 'metal-component/src/Component';
import dom from 'metal-dom/src/dom';
import RequestScreen from 'senna/src/screen/RequestScreen';
import Router from 'metal-router/src/Router';
import Soy from 'metal-soy/src/Soy';
import State from 'metal-state/src/State';
import Uri from 'metal-uri/src/Uri';
import utils from 'senna/src/utils/utils';
import { CancellablePromise } from 'metal-promise/src/promise/Promise';
import { toRegex } from 'metal-path-parser/src/pathParser';

/**
 * Specific Router implementation on top of metal-router to target the specific
 * SoyPortlet architecture.
 *
 * Some added features on top of SPA in Liferay Portal are:
 *  - Support for friendly urls (with patterns)
 *  - Pure SPA navigation after first render (json)
 * 	- Deferred view loading
 *  - Global server error management
 */
class SoyPortletRouter extends State {
	/**
	 * @inheritDoc
	 */
	constructor(config) {
		super(config);

		this.initializeRoutes_();

		const router = this.getRouter();
		router.ignoreQueryStringFromRoutePath = false;
		router.on('endNavigate', this.onEndNavigate_.bind(this));
		router.dispatch();

		Liferay.once('beforeScreenFlip', () => {
			router.dispose();
			Router.routerInstance = null;
			Router.activeRouter = null;
		});
	}

	/**
	 * Gets the currently active component from the current router.
	 * @return {Component}
	 * @see {Router.getActiveComponent}
	 */
	getActiveComponent() {
		return Router.getActiveComponent();
	}

	/**
	 * Gets the state for the currently active component.
	 * @return {Object}
	 * @see {Router.activeState}
	 */
	getActiveState() {
		return Router.activeState;
	}

	/**
	 * Returns the single Senna.js application that handles all `Router`
	 * instances, creating it if it hasn't been built yet.
	 * @return {!App}
	 * @see {Router.router}
	 */
	getRouter() {
		return Router.router();
	}

	/**
	 * Creates the default routing screen for navigation and assigns it as the
	 * Router.defaultScreen value
	 * @protected
	 */
	createDefaultScreen_() {
		const instance = this;

		/**
		 * Special screen that uses attempts to load the controller in a deferred
		 * way after retrieving the new renderState.
		 */
		class DeferredComponentScreen extends Router.defaultScreen {
			/**
			 * @inheritDoc
			 */
			constructor(router) {
				super(router);
			}

			/**
			 * Runs before updating the history path to remove the special
			 * params added to the URL when calculating the fetch version of
			 * the provided one
			 * @param {*} args
			 * @return {string} The stringified URL
			 */
			beforeUpdateHistoryPath(...args) {
				const redirect = RequestScreen.prototype.beforeUpdateHistoryPath.apply(
					this,
					args,
				);

				const uri = new Uri(redirect);
				uri.removeParameter('p_p_lifecycle');
				uri.removeParameter(`${instance.portletNamespace}pjax`);

				if (uri.hasParameter(`${instance.portletNamespace}no_p_p_id`)) {
					uri.removeParameter('p_p_id');
					uri.removeParameter(`${instance.portletNamespace}no_p_p_id`);
				}

				return uri.toString();
			}

			/**
			 * Overrides the default ComponentScreen flip method to continue
			 * only when the controller for the path and all its dependencies
			 * have been successfully loaded. If the load fails , then it falls
			 * back to an ordinary full page navigation.
			 * @return {CancellablePromise} A promise to be resolved once the path
			 * and its needed controllers have been successully loaded or rejected
			 * otherwise
			 */
			flip() {
				const loadedState = super.maybeParseLastLoadedStateAsJson();

				Soy.setInjectedData(loadedState._INJECTED_DATA_);

				const deferred = new CancellablePromise((resolve, reject) => {
					Liferay.Loader.require(
						loadedState.javaScriptLoaderModule,
						(module) => {
							super.maybeRedirectRouter();
							this.router.component = module.default;
							resolve();
						},
						error => reject(error)
					)
				});

				return deferred.then(() => super.flip());
			}
		}

		DeferredComponentScreen.SYNC_UPDATES = true;

		Router.defaultScreen = DeferredComponentScreen;
	}

	/**
	 * Creates a Router for a given route. If the route is the current browser
	 * path, it automatically sets the current state as data and disables
	 * fetching to avoid an extra request.
	 * @param {object} route An object with the route information
	 * @param {Function=} pathFn The path matcher to be used by the router
	 * @protected
	 * @return {Router}
	 */
	createRouter_(route, pathFn) {
		const config = Object.assign({
			cacheable: false,
			element: this.element,
			fetch: true,
			fetchUrl: this.getFetchUrl_.bind(this),
			path:
				pathFn ||
				this.matchPath_.bind(this, route.mvcRenderCommandName),
			portletNamespace: this.portletNamespace,
			route
		});

		try {
			if (config.path(utils.getCurrentBrowserPath())) {
				config.data = this.context;
			}
		} catch (e) {}

		return new Router(config, this.portletWrapper);
	}

	/**
	 * Converts a regular url to its fetch counterpart for spa navigation
	 * @param {string} url The regular url
	 * @protected
	 * @return {string} The equivalent fetch url
	 */
	getFetchUrl_(url) {
		const uri = new Uri(url);

		if (uri.hasParameter('p_p_lifecycle')) {
			const original_p_p_lifecyle = uri.getParameterValue(
				'p_p_lifecycle',
			);

			uri.setParameterValue(
				`${this.portletNamespace}original_p_p_lifecycle`,
				original_p_p_lifecyle,
			);
		}

		uri.setParameterValue('p_p_lifecycle', '2');
		uri.setParameterValue(`${this.portletNamespace}pjax`, true);

		if (!uri.hasParameter('p_p_id')) {
			uri.setParameterValue('p_p_id', this.portletId);
			uri.setParameterValue(`${this.portletNamespace}no_p_p_id`, true);
		}

		return uri.toString();
	}

	/**
	 * Creates a path matcher function for a friendly url pattern
	 * @param {string} pattern Regex pattern for the friendly url
	 * @param {string} mapping Mapping configuration for friendly urls
	 * @protected
	 * @return {Function} A matcher function
	 */
	getPathFunctionForFriendlyURLPattern_(pattern, mapping) {
		return function path(url) {
			const uri = new Uri(url);
			const pathname = uri.getPathname();
			const currentPath = pathname.substring(
				pathname.indexOf(`/${mapping}/`),
			);
			const mappedPath = `/${mapping}${pattern}`;
			return currentPath === mappedPath;
		};
	}

	/**
	 * Creates a path matcher function for a friendly url route
	 * @param {string} friendlyURLRoute The friendly url we want to create a
	 * matcher for
	 * @protected
	 * @return {Function} A matcher function
	 */
	getPathFunctionForFriendlyURLRoute_(friendlyURLRoute) {
		return (
			this.getPathFunctionForImplicitParameters_(
				friendlyURLRoute.implicitParameters,
			) ||
			this.getPathFunctionForFriendlyURLPattern_(
				friendlyURLRoute.pattern,
				this.friendlyURLMapping,
			)
		);
	}

	/**
	 * Creates a path matcher function for a pattern-metal-router implicit param
	 * @param {object} implicitParameters Object with the friendly url implicit
	 * parameters
	 * @protected
	 * @return {Function} A matcher function
	 */
	getPathFunctionForImplicitParameters_(implicitParameters) {
		if (implicitParameters['pattern-metal-router']) {
			const url = `/${this.friendlyURLMapping}${implicitParameters[
				'pattern-metal-router'
			]}`;
			const regex = toRegex(url);
			return regex.test.bind(regex);
		}
	}

	/**
	 * Returns wether the requested MVCRenderCommandName is known or not.
	 * @param {string} mvcRenderCommandName The command name
	 * @protected
	 * @return {boolean}
	 */
	hasMVCRenderCommandName(mvcRenderCommandName) {
		return this.mvcRenderCommandNames.indexOf(mvcRenderCommandName) > -1;
	}

	/**
	 * Initializes routers for all available routes and customizes the default
	 * screen to be used to match the SoyPortlet architecture.
	 * @protected
	 */
	initializeRoutes_() {
		this.createDefaultScreen_();
		this.initializeActionRouter_();
		this.initializeRouters_();
		this.initializeFriendlyURLRouters_();
		this.initializeDefaultRouter_();
	}

	/**
	 * Initializes a router for the action routes of the system.
	 * @protected
	 */
	initializeActionRouter_() {
		const pathFn = url => {
			const uri = new Uri(url);

			const portletIdParam = uri.getParameterValue('p_p_id');
			const lifecycleParam = uri.getParameterValue('p_p_lifecycle');

			return lifecycleParam === '1' && portletIdParam === this.portletId;
		};

		this.createRouter_({}, pathFn);
	}

	/**
	 * Initializes a router for the default route of the system.
	 * @protected
	 */
	initializeDefaultRouter_() {
		const pathFn = this.isDefaultURL_.bind(this);

		this.createRouter_(
			{
				mvcRenderCommandName: '/'
			},
			pathFn,
		);
	}

	/**
	 * Initializes routers for the friendly url routes available in the system.
	 * @protected
	 */
	initializeFriendlyURLRouters_() {
		const routes = this.friendlyURLRoutes.reduce((routes, route) => {
			routes[route.pattern] = route;

			return routes;
		}, {});

		const keys = Object.keys(routes);

		for (let i = 0, l = keys.length; i < l; i++) {
			const pattern = keys[i];
			const friendlyURLRoute = routes[pattern];
			const implicitParameters = friendlyURLRoute.implicitParameters;
			const mvcRenderCommandName = implicitParameters.mvcRenderCommandName;

			if (!this.hasMVCRenderCommandName(mvcRenderCommandName)) {
				console.warn(
					'Unable to find route for mvcRenderCommandName:',
					mvcRenderCommandName,
				);
				continue;
			}

			const pathFn = this.getPathFunctionForFriendlyURLRoute_(friendlyURLRoute);

			this.createRouter_({ mvcRenderCommandName }, pathFn);
		}
	}

	/**
	 * Initializes routers for the regular routes available in the system.
	 * @protected
	 */
	initializeRouters_() {
		this.mvcRenderCommandNames.forEach(mvcRenderCommandName => this.createRouter_({ mvcRenderCommandName }));
	}

	/**
	 * Detects if the supplied url is of a specific type
	 * @param {string} url A path or url
	 * @protected
	 * @return {boolean} True if the supplied url is a simple default url
	 */
	isDefaultURL_(url) {
		const uri = new Uri(url);

		if (uri.getParameterValue('p_p_lifecycle') === '1') {
			return false;
		}

		if (uri.hasParameter(`${this.portletNamespace}javax.portlet.action`)) {
			return false;
		}

		if (uri.hasParameter(`${this.portletNamespace}mvcRenderCommandName`)) {
			return false;
		}

		if (this.isFriendlyURL_(url)) {
			return false;
		}

		const currentURI = new Uri(Liferay.currentURL);

		if (uri.getPathname() === currentURI.getPathname()) {
			return true;
		}

		if (uri.getPathname() === themeDisplay.getLayoutRelativeURL()) {
			return true;
		}

		return false;
	}

	/**
	 * Detects if the supplied url is a registered friendly url
	 * @param {string} url A path or url
	 * @protected
	 * @return {boolean} True if the supplied url is a friendly url
	 */
	isFriendlyURL_(url) {
		const friendlyURLRoute = this.friendlyURLRoutes.find(
			friendlyURLRoute => {
				return this.getPathFunctionForFriendlyURLPattern_(
					friendlyURLRoute.pattern,
					this.friendlyURLMapping,
				)(url);
			},
		);

		return !!friendlyURLRoute;
	}

	/**
	 * Matches a path against a given mvcRenderCommandName to verify if it
	 * can be routed in the given setup
	 * @param {string} mvcRenderCommandName
	 * @param {string} path
	 * @protected
	 * @return {boolean} True if the path matches the mvcRenderCommandName
	 */
	matchPath_(mvcRenderCommandName, path) {
		const uri = new Uri(path);

		const mvcRenderCommandNameParam = uri.getParameterValue(
			`${this.portletNamespace}mvcRenderCommandName`,
		);

		const portletIdParam = uri.getParameterValue('p_p_id');

		return (
			mvcRenderCommandNameParam === mvcRenderCommandName &&
			portletIdParam === this.portletId
		);
	}

	/**
	 * Shows a general alert. Alerts are always discarded once a new navigation
	 * starts
	 * @protected
	 * @param {string} message The message that should appear in the alert
	 * @param {string} type The type of alert (should be danger, warning or
	 * success)
	 */
	maybeShowAlert_(message, type = 'danger') {
		if (message !== undefined) {
			const alert = Component.render(
				Alert,
				{
					body: message,
					dismissible: true,
					elementClasses: 'alert-' + type,
					hideDelay: 30000,
					visible: true,
				},
				this.portletWrapper,
			);

			Router.router().once('startNavigate', () => alert.dispose());
		}
	}

	/**
	 * Handles the global sessionMessages after every navigation
	 * @param {Event} event The end navigation event
	 * @protected
	 */
	onEndNavigate_(event) {
		if (
			event.error &&
			(event.error.requestError || event.error.invalidStatus)
		) {
			window.location.href = event.path;
		} else {
			const activeState = Router.getActiveState();
			if (activeState) {
				const { sessionErrors, sessionMessages } = activeState;

				if (sessionMessages) {
					Object.keys(sessionMessages).forEach(key =>
						this.maybeShowAlert_(sessionMessages[key], 'warning'),
					);
				}

				if (sessionErrors) {
					Object.keys(sessionErrors).forEach(key =>
						this.maybeShowAlert_(sessionErrors[key]),
					);
				}
			}
		}
	}
}

/**
 * State definition.
 * @ignore
 * @type {!Object}
 * @static
 */
SoyPortletRouter.STATE = {
	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {Object}
	 */
	context: {},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {string}
	 */
	element: {
		setter: dom.toElement
	},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {Array<string>}
	 */
	friendlyURLRoutes: {},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {string}
	 */
	friendlyURLMapping: {},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {string}
	 */
	friendlyURLPrefix: {},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {Array<String>}
	 */
	mvcRenderCommandNames: {},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {string}
	 */
	portletId: {},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {string}
	 */
	portletNamespace: {},

	/**
	 * @instance
	 * @memberof SoyPortletRouter
	 * @type {string}
	 */
	portletWrapper: {
		setter: dom.toElement,
	}
};

export default SoyPortletRouter;
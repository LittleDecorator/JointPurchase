!function () {
	angular.module("angularytics", []).provider("Angularytics", function () {
		var e = ["Google"];
		this.setEventHandlers = function (a) {
			angular.isString(a) && (a = [a]), e = [], angular.forEach(a, function (a) {
				e.push(t(a))
			})
		};
		var t = function (e) {
			return e.charAt(0).toUpperCase() + e.substring(1)
		}, a = "$locationChangeSuccess";
		this.setPageChangeEvent = function (e) {
			a = e
		}, this.$get = ["$injector", "$rootScope", "$location", function (t, n, o) {
			var l = [];
			angular.forEach(e, function (e) {
				l.push(t.get("Angularytics" + e + "Handler"))
			});
			var i = function (e) {
				angular.forEach(l, function (t) {
					e(t)
				})
			}, s = {};
			return s.init = function () {
			}, s.trackEvent = function (e, t, a, n, o) {
				i(function (l) {
					e && t && l.trackEvent(e, t, a, n, o)
				})
			}, s.trackPageView = function (e) {
				i(function (t) {
					e && t.trackPageView(e)
				})
			}, s.trackTiming = function (e, t, a, n) {
				i(function (o) {
					e && t && a && o.trackTiming(e, t, a, n)
				})
			}, n.$on(a, function () {
				s.trackPageView(o.url())
			}), s
		}]
	})
}(), function () {
	angular.module("angularytics").factory("AngularyticsConsoleHandler", ["$log", function (e) {
		var t = {};
		return t.trackPageView = function (t) {
			e.log("URL visited", t)
		}, t.trackEvent = function (t, a, n, o, l) {
			e.log("Event tracked", t, a, n, o, l)
		}, t.trackTiming = function (t, a, n, o) {
			e.log("Timing tracked", t, a, n, o)
		}, t
	}])
}(), function () {
	angular.module("angularytics").factory("AngularyticsGoogleHandler", function () {
		var e = {};
		return e.trackPageView = function (e) {
			_gaq.push(["_set", "page", e]), _gaq.push(["_trackPageview", e])
		}, e.trackEvent = function (e, t, a, n, o) {
			_gaq.push(["_trackEvent", e, t, a, n, o])
		}, e.trackTiming = function (e, t, a, n) {
			_gaq.push(["_trackTiming", e, t, a, n])
		}, e
	}).factory("AngularyticsGoogleUniversalHandler", function () {
		var e = {};
		return e.trackPageView = function (e) {
			ga("set", "page", e), ga("send", "pageview", e)
		}, e.trackEvent = function (e, t, a, n, o) {
			ga("send", "event", e, t, a, n, {nonInteraction: o})
		}, e.trackTiming = function (e, t, a, n) {
			ga("send", "timing", e, t, a, n)
		}, e
	})
}(), function () {
	angular.module("angularytics").filter("trackEvent", ["Angularytics", function (e) {
		return function (t, a, n, o, l, i) {
			return e.trackEvent(a, n, o, l, i), t
		}
	}])
}(), angular.module("docsApp", ["angularytics", "ngRoute", "ngMessages", "ngMaterial"], ["SERVICES", "COMPONENTS", "DEMOS", "PAGES", "$routeProvider", "$locationProvider", "$mdThemingProvider", "$mdIconProvider", function (e, t, a, n, o, l, i, s) {
	l.html5Mode(!0), o.when("/", {templateUrl: "partials/home.tmpl.html"}).when("/layout/:tmpl", {
		templateUrl: function (e) {
			return "partials/layout-" + e.tmpl + ".tmpl.html"
		}
	}).when("/layout/", {redirectTo: "/layout/introduction"}).when("/demo/", {redirectTo: a[0].url}).when("/api/", {redirectTo: t[0].docs[0].url}).when("/getting-started", {templateUrl: "partials/getting-started.tmpl.html"}).when("/contributors", {templateUrl: "partials/contributors.tmpl.html"}).when("/license", {templateUrl: "partials/license.tmpl.html"}), i.definePalette("docs-blue", i.extendPalette("blue", {
		50: "#DCEFFF",
		100: "#AAD1F9",
		200: "#7BB8F5",
		300: "#4C9EF1",
		400: "#1C85ED",
		500: "#106CC8",
		600: "#0159A2",
		700: "#025EE9",
		800: "#014AB6",
		900: "#013583",
		contrastDefaultColor: "light",
		contrastDarkColors: "50 100 200 A100",
		contrastStrongLightColors: "300 400 A200 A400"
	})), i.definePalette("docs-red", i.extendPalette("red", {A100: "#DE3641"})), i.theme("docs-dark", "default").primaryPalette("yellow").dark(), s.icon("md-toggle-arrow", "img/icons/toggle-arrow.svg", 48), i.theme("default").primaryPalette("docs-blue").accentPalette("docs-red"), i.enableBrowserColor(), angular.forEach(n, function (e, t) {
		angular.forEach(e, function (e) {
			o.when(e.url, {templateUrl: e.outputPath, controller: "GuideCtrl"})
		})
	}), angular.forEach(t, function (e) {
		angular.forEach(e.docs, function (t) {
			o.when("/" + t.url, {
				templateUrl: t.outputPath, resolve: {
					component: function () {
						return e
					}, doc: function () {
						return t
					}
				}, controller: "ComponentDocCtrl"
			})
		})
	}), angular.forEach(e, function (e) {
		o.when("/" + e.url, {
			templateUrl: e.outputPath, resolve: {
				component: function () {
					return {isService: !0}
				}, doc: function () {
					return e
				}
			}, controller: "ComponentDocCtrl"
		})
	}), angular.forEach(a, function (e) {
		var a;
		t.forEach(function (t) {
			e.moduleName === t.name && (a = t, t.demoUrl = e.url)
		}), a = a || angular.extend({}, e), o.when("/" + e.url, {
			templateUrl: "partials/demo.tmpl.html",
			controller: "DemoCtrl",
			resolve: {
				component: function () {
					return a
				}, demos: function () {
					return e.demos
				}
			}
		})
	}), o.otherwise("/"), l.hashPrefix("!")
}]).config(["AngularyticsProvider", function (e) {
	e.setEventHandlers(["Console", "GoogleUniversal"])
}]).run(["Angularytics", function (e) {
	e.init()
}]).factory("menu", ["SERVICES", "COMPONENTS", "DEMOS", "PAGES", "$location", "$rootScope", "$http", "$window", function (e, t, a, n, o, l, i, s) {
	function r(e, t) {
		return e.name < t.name ? -1 : 1
	}

	function m() {
		var e = o.path(), t = {name: "Introduction", url: "/", type: "link"};
		if ("/" == e)return b.selectSection(t), void b.selectPage(t, t);
		var a = function (t, a) {
			e.indexOf(a.url) !== -1 && (b.selectSection(t), b.selectPage(t, a))
		};
		c.forEach(function (e) {
			e.children ? e.children.forEach(function (e) {
				e.pages && e.pages.forEach(function (t) {
					a(e, t)
				})
			}) : e.pages ? e.pages.forEach(function (t) {
				a(e, t)
			}) : "link" === e.type && a(e, e)
		})
	}

	var d = {}, c = [{name: "Getting Started", url: "getting-started", type: "link"}], p = [];
	angular.forEach(a, function (e) {
		p.push({name: e.label, url: e.url})
	}), c.push({name: "Demos", pages: p.sort(r), type: "toggle"}), c.push(), c.push({
		name: "Customization",
		type: "heading",
		children: [{
			name: "CSS",
			type: "toggle",
			pages: [{name: "Typography", url: "CSS/typography", type: "link"}, {name: "Button", url: "CSS/button", type: "link"}, {
				name: "Checkbox",
				url: "CSS/checkbox",
				type: "link"
			}]
		}, {
			name: "Theming",
			type: "toggle",
			pages: [{name: "Introduction and Terms", url: "Theming/01_introduction", type: "link"}, {
				name: "Declarative Syntax",
				url: "Theming/02_declarative_syntax",
				type: "link"
			}, {name: "Configuring a Theme", url: "Theming/03_configuring_a_theme", type: "link"}, {
				name: "Multiple Themes",
				url: "Theming/04_multiple_themes",
				type: "link"
			}, {name: "Under the Hood", url: "Theming/05_under_the_hood", type: "link"}, {name: "Browser Color", url: "Theming/06_browser_color", type: "link"}]
		}]
	});
	var u = {}, h = {};
	t.forEach(function (e) {
		e.docs.forEach(function (e) {
			angular.isDefined(e["private"]) || (h[e.type] = h[e.type] || [], h[e.type].push(e), u[e.module] = u[e.module] || [], u[e.module].push(e))
		})
	}), e.forEach(function (e) {
		angular.isDefined(e["private"]) || (h[e.type] = h[e.type] || [], h[e.type].push(e), u[e.module] = u[e.module] || [], u[e.module].push(e))
	}), c.push({
		name: "API Reference",
		type: "heading",
		children: [{
			name: "Layout",
			type: "toggle",
			pages: [{name: "Introduction", id: "layoutIntro", url: "layout/introduction"}, {
				name: "Layout Containers",
				id: "layoutContainers",
				url: "layout/container"
			}, {name: "Layout Children", id: "layoutGrid", url: "layout/children"}, {
				name: "Child Alignment",
				id: "layoutAlign",
				url: "layout/alignment"
			}, {name: "Extra Options", id: "layoutOptions", url: "layout/options"}, {name: "Troubleshooting", id: "layoutTips", url: "layout/tips"}]
		}, {name: "Services", pages: h.service.sort(r), type: "toggle"}, {name: "Types", pages: h.type.sort(r), type: "toggle"}, {
			name: "Directives",
			pages: h.directive.sort(r),
			type: "toggle"
		}]
	}), c.push({name: "Contributors", url: "contributors", type: "link"}), c.push({name: "License", url: "license", type: "link", hidden: !0});
	var b;
	return l.$on("$locationChangeSuccess", m), i.get("/docs.json").then(function (e) {
		function t(e) {
			switch (i) {
				case e.id:
					return !1;
				case"latest":
					return !e.latest;
				default:
					return !0
			}
		}

		function a() {
			return e && e.versions && e.versions.length ? e.versions.map(function (t) {
				var a = e.latest === t;
				return {type: "version", url: "/" + t, name: n({id: t, latest: a}), id: t, latest: a, github: "tree/v" + t}
			}) : []
		}

		function n(e) {
			return e.latest ? "Latest Release (" + e.id + ")" : "Release " + e.id
		}

		function o() {
			function e(e) {
				return e.latest
			}

			function t(e) {
				return i === e.id
			}

			switch (i) {
				case"head":
					return r;
				case"latest":
					return p.filter(e)[0];
				default:
					return p.filter(t)[0]
			}
		}

		function l() {
			var e = s.location.pathname;
			return e.length < 2 && (e = "HEAD"), e.match(/[^\/]+/)[0].toLowerCase()
		}

		e = e.data;
		var i = l(), r = {
			type: "version",
			url: "/HEAD",
			id: "head",
			name: "HEAD (master)",
			github: ""
		}, m = "head" === i ? [] : [r], p = a(), u = p.filter(t), h = o() || {name: "local"};
		d.current = h, c.unshift({
			name: "Documentation Version",
			type: "heading",
			className: "version-picker",
			children: [{name: h.name, type: "toggle", pages: m.concat(u)}]
		})
	}), b = {
		version: d, sections: c, selectSection: function (e) {
			b.openedSection = e
		}, toggleSelectSection: function (e) {
			b.openedSection = b.openedSection === e ? null : e
		}, isSectionSelected: function (e) {
			return b.openedSection === e
		}, selectPage: function (e, t) {
			b.currentSection = e, b.currentPage = t
		}, isPageSelected: function (e) {
			return b.currentPage === e
		}
	}
}]).directive("menuLink", function () {
	return {
		scope: {section: "="}, templateUrl: "partials/menu-link.tmpl.html", link: function (e, t) {
			var a = t.parent().controller();
			e.isSelected = function () {
				return a.isSelected(e.section)
			}, e.focusSection = function () {
				a.autoFocusContent = !0
			}
		}
	}
}).directive("menuToggle", ["$mdUtil", "$animateCss", "$$rAF", function (e, t, a) {
	return {
		scope: {section: "="}, templateUrl: "partials/menu-toggle.tmpl.html", link: function (n, o) {
			var l = o.parent().controller();
			n.renderContent = !1, n.isOpen = function () {
				return l.isOpen(n.section)
			}, n.toggle = function () {
				l.toggleOpen(n.section)
			}, e.nextTick(function () {
				n.$watch(function () {
					return l.isOpen(n.section)
				}, function (l) {
					var i = o.find("ul");
					i[0].querySelector("a.active");
					l && (n.renderContent = !0), a(function () {
						var a = l ? i[0].scrollHeight : 0;
						t(i, {easing: "cubic-bezier(0.35, 0, 0.25, 1)", to: {height: a + "px"}, duration: .75}).start().then(function () {
							var t = i[0].querySelector("a.active");
							if (n.renderContent = l, l && t && 0 === i[0].scrollTop) {
								var a = t.scrollHeight, o = t.offsetTop, s = t.offsetParent, r = s ? s.offsetTop : 0, m = 2 * a, d = o + r - m;
								e.animateScrollTo(document.querySelector(".docs-menu").parentNode, d)
							}
						})
					})
				})
			});
			var i = o[0].parentNode.parentNode.parentNode;
			if (i.classList.contains("parent-list-item")) {
				var s = i.querySelector("h2");
				o[0].firstChild.setAttribute("aria-describedby", s.id)
			}
		}
	}
}]).controller("DocsCtrl", ["$scope", "COMPONENTS", "BUILDCONFIG", "$mdSidenav", "$timeout", "$mdDialog", "menu", "$location", "$rootScope", "$mdUtil", function (e, t, a, n, o, l, i, s, r, m) {
	function d() {
		o(function () {
			n("left").close()
		})
	}

	function c() {
		o(function () {
			n("left").open()
		})
	}

	function p() {
		return s.path()
	}

	function u() {
		m.animateScrollTo(P, 0, 200)
	}

	function h(e) {
		i.selectPage(null, null), s.path("/")
	}

	function b() {
		e.closeMenu(), w.autoFocusContent && (g(), w.autoFocusContent = !1)
	}

	function g(e) {
		e && e.preventDefault(), o(function () {
			j.focus()
		}, 90)
	}

	function f(e) {
		return i.isPageSelected(e)
	}

	function y(e) {
		var t = !1, a = i.openedSection;
		return a === e ? t = !0 : e.children && e.children.forEach(function (e) {
			e === a && (t = !0)
		}), t
	}

	function v(e) {
		return i.isSectionSelected(e)
	}

	function x(e) {
		i.toggleSelectSection(e)
	}

	var w = this;
	e.COMPONENTS = t, e.BUILDCONFIG = a, e.menu = i, e.path = p, e.goHome = h, e.openMenu = c, e.closeMenu = d, e.isSectionSelected = y, e.scrollTop = u, e.thisYear = (new Date).getFullYear(), r.$on("$locationChangeSuccess", b), e.focusMainContent = g, Object.defineProperty(r, "relatedPage", {
		get: function () {
			return null
		}, set: angular.noop, enumerable: !0, configurable: !0
	}), r.redirectToUrl = function (e) {
		s.path(e), o(function () {
			r.relatedPage = null
		}, 100)
	}, this.isOpen = v, this.isSelected = f, this.toggleOpen = x, this.autoFocusContent = !1;
	var j = document.querySelector("[role='main']"), P = j.querySelector("md-content[md-scroll-y]")
}]).controller("HomeCtrl", ["$scope", "$rootScope", function (e, t) {
	t.currentComponent = t.currentDoc = null
}]).controller("GuideCtrl", ["$rootScope", "$http", function (e, t) {
	e.currentComponent = e.currentDoc = null, e.contributors || t.get("./contributors.json").then(function (t) {
		e.github = t.data
	})
}]).controller("LayoutCtrl", ["$scope", "$attrs", "$location", "$rootScope", function (e, t, a, n) {
	n.currentComponent = n.currentDoc = null, e.exampleNotEditable = !0, e.layoutDemo = {
		mainAxis: "center",
		crossAxis: "center",
		direction: "row"
	}, e.layoutAlign = function () {
		return e.layoutDemo.mainAxis + (e.layoutDemo.crossAxis ? " " + e.layoutDemo.crossAxis : "")
	}
}]).controller("LayoutTipsCtrl", [function () {
	var e = this;
	e.toggleButtonText = "Hide", e.toggleContentSize = function () {
		var t = angular.element(document.getElementById("toHide"));
		t.toggleClass("ng-hide"), e.toggleButtonText = t.hasClass("ng-hide") ? "Show" : "Hide"
	}
}]).controller("ComponentDocCtrl", ["$scope", "doc", "component", "$rootScope", function (e, t, a, n) {
	n.currentComponent = a, n.currentDoc = t
}]).controller("DemoCtrl", ["$rootScope", "$scope", "component", "demos", "$templateRequest", function (e, t, a, n, o) {
	e.currentComponent = a, e.currentDoc = null, t.demos = [], angular.forEach(n, function (e) {
		var a = [e.index].concat(e.js || []).concat(e.css || []).concat(e.html || []);
		a.forEach(function (e) {
			e.httpPromise = o(e.outputPath).then(function (t) {
				return e.contents = t.replace("<head/>", ""), e.contents
			})
		}), e.$files = a, t.demos.push(e)
	}), t.demos = t.demos.sort(function (e, t) {
		return e.name > t.name ? 1 : -1
	})
}]).filter("nospace", function () {
	return function (e) {
		return e ? e.replace(/ /g, "") : ""
	}
}).filter("humanizeDoc", function () {
	return function (e) {
		if (e)return "directive" === e.type ? e.name.replace(/([A-Z])/g, function (e) {
			return "-" + e.toLowerCase()
		}) : e.label || e.name
	}
}).filter("directiveBrackets", function () {
	return function (e, t) {
		if (t) {
			if (!t.element && t.attribute)return "[" + e + "]";
			if (t.element && e.indexOf("-") > -1)return "<" + e + ">"
		}
		return e
	}
}).directive("docsScrollClass", function () {
	return {
		restrict: "A", link: function (e, t, a) {
			function n() {
				var e = 0 !== o[0].scrollTop;
				e !== l && t.toggleClass(a.docsScrollClass, e), l = e
			}

			var o = t.parent(), l = !1;
			n(), o.on("scroll", n)
		}
	}
}), function () {
	function e(e, t) {
		function a(a, o, l, i) {
			function s() {
				a.name = o.text().trim().replace(/'/g, "").replace(n, "-").replace(/-{2,}/g, "-").replace(/^-|-$/g, "").toLowerCase()
			}

			if (i) {
				var r = t('<a class="docs-anchor" ng-href="#{{ name }}" name="{{ name }}"></a>')(a);
				r.append(o.contents()), o.append(r), e.nextTick(s)
			}
		}

		var n = /[&\s+$,:;=?@"#{}|^~[`%!'\].\/()*\\]/g;
		return {restrict: "E", scope: {}, require: "^?mdContent", link: a}
	}

	angular.module("docsApp").directive("h4", e).directive("h3", e).directive("h2", e).directive("h1", e), e.$inject = ["$mdUtil", "$compile"]
}(), angular.module("docsApp").constant("BUILDCONFIG", {
	ngVersion: "1.5.5",
	version: "1.1.4",
	repository: "https://github.com/angular/material",
	commit: "666eb1d57717ecb4e356ebdaeb39ac304381f163",
	date: "2017-04-20 14:47:24 -0700"
}), function () {
	function e(e, t, a) {
		function n(n) {
			var l = a.translate(n, e.all()), i = o(l);
			t.find("body").append(i), i[0].submit(), i.remove()
		}

		function o(e) {
			var t = angular.element('<form style="display: none;" method="post" target="_blank" action="' + i + '"></form>'), a = '<input type="hidden" name="data" value="' + l(e) + '" />';
			return t.append(a), t
		}

		function l(e) {
			return JSON.stringify(e).replace(/'/g, "&amp;apos;").replace(/"/g, "&amp;quot;").replace(/&amp;lt;/g, "&#x02C2;").replace(/&amp;gt;/g, "&#x02C3;")
		}

		var i = "http://codepen.io/pen/define/";
		return {editOnCodepen: n}
	}

	function t() {
		function e(e, l) {
			var i = e.files;
			return a({
				title: e.title,
				html: t(e),
				head: p,
				js: n(i.js),
				css: o(i.css).join(" "),
				js_external: l.concat([u, m]).join(";"),
				css_external: [d, c].join(";")
			})
		}

		function t(e) {
			var t = e.files.index.contents, a = [l, i, s];
			return a.forEach(function (a) {
				t = a(t, e)
			}), t
		}

		function a(e) {
			function t(e, t) {
				var a = "", n = "";
				switch (t) {
					case"html":
						a = "<!--", n = "-->";
						break;
					case"js":
						a = "/**", n = "**/";
						break;
					case"css":
						a = "/*", n = "*/"
				}
				return e + "\n\n" + a + "\nCopyright 2016 Google Inc. All Rights Reserved. \nUse of this source code is governed by an MIT-style license that can be foundin the LICENSE file at http://material.angularjs.org/HEAD/license.\n" + n
			}

			return e.html = t(e.html, "html"), e.js = t(e.js, "js"), e.css = t(e.css, "css"), e
		}

		function n(e) {
			var t = o(e).join(" "), a = r(t);
			return a
		}

		function o(e) {
			return e.map(function (e) {
				return e.contents
			})
		}

		function l(e, t) {
			var a;
			return angular.forEach(angular.element(e), function (e, t) {
				"SCRIPT" != e.nodeName && "#text" != e.nodeName && (a = angular.element(e))
			}), a.addClass(t.id), a.attr("ng-app", "MyApp"), a[0].outerHTML
		}

		function i(e, t) {
			if (t.files.html.length) {
				var a = angular.element(e);
				return angular.forEach(t.files.html, function (e) {
					a.append("<script type='text/ng-template' id='" + e.name + "'>" + e.contents + "</script>")
				}), a[0].outerHTML
			}
			return e
		}

		function s(e) {
			return e.replace(/&/g, "&amp;")
		}

		function r(e) {
			var t = /\.module\(('[^']*'|"[^"]*")\s*,(\s*\[([^\]]*)\]\s*\))/gi, a = "['ngMaterial', 'ngMessages', 'material.svgAssetsCache']";
			return e.replace(t, ".module('MyApp'," + a + ")")
		}

		var m = "https://cdn.gitcdn.link/cdn/angular/bower-material/v1.1.4/angular-material.js", d = "https://cdn.gitcdn.link/cdn/angular/bower-material/v1.1.4/angular-material.css", c = "https://material.angularjs.org/1.1.4/docs.css", p = '<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic">', u = "https://s3-us-west-2.amazonaws.com/s.cdpn.io/t-114/svg-assets-cache.js";
		return {translate: e}
	}

	angular.module("docsApp").factory("codepenDataAdapter", t).factory("codepen", ["$demoAngularScripts", "$document", "codepenDataAdapter", e])
}(), angular.module("docsApp").constant("COMPONENTS", [{
	name: "material.components.autocomplete",
	type: "module",
	outputPath: "partials/api/material.components.autocomplete/index.html",
	url: "api/material.components.autocomplete",
	label: "material.components.autocomplete",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/autocomplete/autocomplete.js",
	docs: [{
		name: "mdAutocomplete",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.autocomplete/directive/mdAutocomplete.html",
		url: "api/directive/mdAutocomplete",
		label: "mdAutocomplete",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/autocomplete/js/autocompleteDirective.js",
		hasDemo: !0
	}, {
		name: "mdHighlightText",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.autocomplete/directive/mdHighlightText.html",
		url: "api/directive/mdHighlightText",
		label: "mdHighlightText",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/autocomplete/js/highlightDirective.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.bottomSheet",
	type: "module",
	outputPath: "partials/api/material.components.bottomSheet/index.html",
	url: "api/material.components.bottomSheet",
	label: "material.components.bottomSheet",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/bottomSheet/bottom-sheet.js",
	docs: [{
		name: "$mdBottomSheet",
		type: "service",
		outputPath: "partials/api/material.components.bottomSheet/service/$mdBottomSheet.html",
		url: "api/service/$mdBottomSheet",
		label: "$mdBottomSheet",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/bottomSheet/bottom-sheet.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.components.button",
	type: "module",
	outputPath: "partials/api/material.components.button/index.html",
	url: "api/material.components.button",
	label: "material.components.button",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/button/button.js",
	docs: [{
		name: "mdButton",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.button/directive/mdButton.html",
		url: "api/directive/mdButton",
		label: "mdButton",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/button/button.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.card",
	type: "module",
	outputPath: "partials/api/material.components.card/index.html",
	url: "api/material.components.card",
	label: "material.components.card",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/card/card.js",
	docs: [{
		name: "mdCard",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.card/directive/mdCard.html",
		url: "api/directive/mdCard",
		label: "mdCard",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/card/card.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.checkbox",
	type: "module",
	outputPath: "partials/api/material.components.checkbox/index.html",
	url: "api/material.components.checkbox",
	label: "material.components.checkbox",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/checkbox/checkbox.js",
	docs: [{
		name: "mdCheckbox",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.checkbox/directive/mdCheckbox.html",
		url: "api/directive/mdCheckbox",
		label: "mdCheckbox",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/checkbox/checkbox.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.chips",
	type: "module",
	outputPath: "partials/api/material.components.chips/index.html",
	url: "api/material.components.chips",
	label: "material.components.chips",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/chips/chips.js",
	docs: [{
		name: "mdChip",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.chips/directive/mdChip.html",
		url: "api/directive/mdChip",
		label: "mdChip",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/chips/js/chipDirective.js",
		hasDemo: !0
	}, {
		name: "mdChipRemove",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.chips/directive/mdChipRemove.html",
		url: "api/directive/mdChipRemove",
		label: "mdChipRemove",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/chips/js/chipRemoveDirective.js",
		hasDemo: !0
	}, {
		name: "mdChips",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.chips/directive/mdChips.html",
		url: "api/directive/mdChips",
		label: "mdChips",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/chips/js/chipsDirective.js",
		hasDemo: !0
	}, {
		name: "mdContactChips",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.chips/directive/mdContactChips.html",
		url: "api/directive/mdContactChips",
		label: "mdContactChips",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/chips/js/contactChipsDirective.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.colors",
	type: "module",
	outputPath: "partials/api/material.components.colors/index.html",
	url: "api/material.components.colors",
	label: "material.components.colors",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/colors/colors.js",
	docs: [{
		name: "$mdColors",
		type: "service",
		outputPath: "partials/api/material.components.colors/service/$mdColors.html",
		url: "api/service/$mdColors",
		label: "$mdColors",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/colors/colors.js",
		hasDemo: !1
	}, {
		name: "mdColors",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.colors/directive/mdColors.html",
		url: "api/directive/mdColors",
		label: "mdColors",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/colors/colors.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.content",
	type: "module",
	outputPath: "partials/api/material.components.content/index.html",
	url: "api/material.components.content",
	label: "material.components.content",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/content/content.js",
	docs: [{
		name: "mdContent",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.content/directive/mdContent.html",
		url: "api/directive/mdContent",
		label: "mdContent",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/content/content.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.datepicker",
	type: "module",
	outputPath: "partials/api/material.components.datepicker/index.html",
	url: "api/material.components.datepicker",
	label: "material.components.datepicker",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/datepicker/datePicker.js",
	docs: [{
		name: "mdCalendar",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.datepicker/directive/mdCalendar.html",
		url: "api/directive/mdCalendar",
		label: "mdCalendar",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/datepicker/js/calendar.js",
		hasDemo: !0
	}, {
		name: "$mdDateLocaleProvider",
		type: "service",
		outputPath: "partials/api/material.components.datepicker/service/$mdDateLocaleProvider.html",
		url: "api/service/$mdDateLocaleProvider",
		label: "$mdDateLocaleProvider",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/datepicker/js/dateLocaleProvider.js",
		hasDemo: !1
	}, {
		name: "mdDatepicker",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.datepicker/directive/mdDatepicker.html",
		url: "api/directive/mdDatepicker",
		label: "mdDatepicker",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/datepicker/js/datepickerDirective.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.dialog",
	type: "module",
	outputPath: "partials/api/material.components.dialog/index.html",
	url: "api/material.components.dialog",
	label: "material.components.dialog",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/dialog/dialog.js",
	docs: [{
		name: "mdDialog",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.dialog/directive/mdDialog.html",
		url: "api/directive/mdDialog",
		label: "mdDialog",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/dialog/dialog.js",
		hasDemo: !0
	}, {
		name: "$mdDialog",
		type: "service",
		outputPath: "partials/api/material.components.dialog/service/$mdDialog.html",
		url: "api/service/$mdDialog",
		label: "$mdDialog",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/dialog/dialog.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.components.divider",
	type: "module",
	outputPath: "partials/api/material.components.divider/index.html",
	url: "api/material.components.divider",
	label: "material.components.divider",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/divider/divider.js",
	docs: [{
		name: "mdDivider",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.divider/directive/mdDivider.html",
		url: "api/directive/mdDivider",
		label: "mdDivider",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/divider/divider.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.fabActions",
	type: "module",
	outputPath: "partials/api/material.components.fabActions/index.html",
	url: "api/material.components.fabActions",
	label: "material.components.fabActions",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/fabActions/fabActions.js",
	docs: [{
		name: "mdFabActions",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.fabActions/directive/mdFabActions.html",
		url: "api/directive/mdFabActions",
		label: "mdFabActions",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/fabActions/fabActions.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.fabSpeedDial",
	type: "module",
	outputPath: "partials/api/material.components.fabSpeedDial/index.html",
	url: "api/material.components.fabSpeedDial",
	label: "material.components.fabSpeedDial",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/fabSpeedDial/fabSpeedDial.js",
	docs: [{
		name: "mdFabSpeedDial",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.fabSpeedDial/directive/mdFabSpeedDial.html",
		url: "api/directive/mdFabSpeedDial",
		label: "mdFabSpeedDial",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/fabSpeedDial/fabSpeedDial.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.fabToolbar",
	type: "module",
	outputPath: "partials/api/material.components.fabToolbar/index.html",
	url: "api/material.components.fabToolbar",
	label: "material.components.fabToolbar",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/fabToolbar/fabToolbar.js",
	docs: [{
		name: "mdFabToolbar",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.fabToolbar/directive/mdFabToolbar.html",
		url: "api/directive/mdFabToolbar",
		label: "mdFabToolbar",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/fabToolbar/fabToolbar.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.gridList",
	type: "module",
	outputPath: "partials/api/material.components.gridList/index.html",
	url: "api/material.components.gridList",
	label: "material.components.gridList",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/gridList/grid-list.js",
	docs: [{
		name: "mdGridList",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.gridList/directive/mdGridList.html",
		url: "api/directive/mdGridList",
		label: "mdGridList",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/gridList/grid-list.js",
		hasDemo: !0
	}, {
		name: "mdGridTile",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.gridList/directive/mdGridTile.html",
		url: "api/directive/mdGridTile",
		label: "mdGridTile",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/gridList/grid-list.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.icon",
	type: "module",
	outputPath: "partials/api/material.components.icon/index.html",
	url: "api/material.components.icon",
	label: "material.components.icon",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/icon/icon.js",
	docs: [{
		name: "mdIcon",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.icon/directive/mdIcon.html",
		url: "api/directive/mdIcon",
		label: "mdIcon",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/icon/js/iconDirective.js",
		hasDemo: !0
	}, {
		name: "$mdIconProvider",
		type: "service",
		outputPath: "partials/api/material.components.icon/service/$mdIconProvider.html",
		url: "api/service/$mdIconProvider",
		label: "$mdIconProvider",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/icon/js/iconService.js",
		hasDemo: !1
	}, {
		name: "$mdIcon",
		type: "service",
		outputPath: "partials/api/material.components.icon/service/$mdIcon.html",
		url: "api/service/$mdIcon",
		label: "$mdIcon",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/icon/js/iconService.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.components.input",
	type: "module",
	outputPath: "partials/api/material.components.input/index.html",
	url: "api/material.components.input",
	label: "material.components.input",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/input/input.js",
	docs: [{
		name: "mdInputContainer",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.input/directive/mdInputContainer.html",
		url: "api/directive/mdInputContainer",
		label: "mdInputContainer",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/input/input.js",
		hasDemo: !0
	}, {
		name: "mdInput",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.input/directive/mdInput.html",
		url: "api/directive/mdInput",
		label: "mdInput",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/input/input.js",
		hasDemo: !0
	}, {
		name: "mdSelectOnFocus",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.input/directive/mdSelectOnFocus.html",
		url: "api/directive/mdSelectOnFocus",
		label: "mdSelectOnFocus",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/input/input.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.list",
	type: "module",
	outputPath: "partials/api/material.components.list/index.html",
	url: "api/material.components.list",
	label: "material.components.list",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/list/list.js",
	docs: [{
		name: "mdList",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.list/directive/mdList.html",
		url: "api/directive/mdList",
		label: "mdList",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/list/list.js",
		hasDemo: !0
	}, {
		name: "mdListItem",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.list/directive/mdListItem.html",
		url: "api/directive/mdListItem",
		label: "mdListItem",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/list/list.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.menu",
	type: "module",
	outputPath: "partials/api/material.components.menu/index.html",
	url: "api/material.components.menu",
	label: "material.components.menu",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/menu/menu.js",
	docs: [{
		name: "mdMenu",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.menu/directive/mdMenu.html",
		url: "api/directive/mdMenu",
		label: "mdMenu",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/menu/js/menuDirective.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.menuBar",
	type: "module",
	outputPath: "partials/api/material.components.menuBar/index.html",
	url: "api/material.components.menuBar",
	label: "material.components.menuBar",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/menuBar/menu-bar.js",
	docs: [{
		name: "mdMenuBar",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.menuBar/directive/mdMenuBar.html",
		url: "api/directive/mdMenuBar",
		label: "mdMenuBar",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/menuBar/js/menuBarDirective.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.navBar",
	type: "module",
	outputPath: "partials/api/material.components.navBar/index.html",
	url: "api/material.components.navBar",
	label: "material.components.navBar",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/navBar/navBar.js",
	docs: [{
		name: "mdNavBar",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.navBar/directive/mdNavBar.html",
		url: "api/directive/mdNavBar",
		label: "mdNavBar",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/navBar/navBar.js",
		hasDemo: !0
	}, {
		name: "mdNavItem",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.navBar/directive/mdNavItem.html",
		url: "api/directive/mdNavItem",
		label: "mdNavItem",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/navBar/navBar.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.panel",
	type: "module",
	outputPath: "partials/api/material.components.panel/index.html",
	url: "api/material.components.panel",
	label: "material.components.panel",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/panel/panel.js",
	docs: [{
		name: "$mdPanelProvider",
		type: "service",
		outputPath: "partials/api/material.components.panel/service/$mdPanelProvider.html",
		url: "api/service/$mdPanelProvider",
		label: "$mdPanelProvider",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/panel/panel.js",
		hasDemo: !1
	}, {
		name: "$mdPanel",
		type: "service",
		outputPath: "partials/api/material.components.panel/service/$mdPanel.html",
		url: "api/service/$mdPanel",
		label: "$mdPanel",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/panel/panel.js",
		hasDemo: !1
	}, {
		name: "MdPanelRef",
		type: "type",
		outputPath: "partials/api/material.components.panel/type/MdPanelRef.html",
		url: "api/type/MdPanelRef",
		label: "MdPanelRef",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/panel/panel.js",
		hasDemo: !1
	}, {
		name: "MdPanelPosition",
		type: "type",
		outputPath: "partials/api/material.components.panel/type/MdPanelPosition.html",
		url: "api/type/MdPanelPosition",
		label: "MdPanelPosition",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/panel/panel.js",
		hasDemo: !1
	}, {
		name: "MdPanelAnimation",
		type: "type",
		outputPath: "partials/api/material.components.panel/type/MdPanelAnimation.html",
		url: "api/type/MdPanelAnimation",
		label: "MdPanelAnimation",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/panel/panel.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.components.progressCircular",
	type: "module",
	outputPath: "partials/api/material.components.progressCircular/index.html",
	url: "api/material.components.progressCircular",
	label: "material.components.progressCircular",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/progressCircular/progress-circular.js",
	docs: [{
		name: "mdProgressCircular",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.progressCircular/directive/mdProgressCircular.html",
		url: "api/directive/mdProgressCircular",
		label: "mdProgressCircular",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/progressCircular/js/progressCircularDirective.js",
		hasDemo: !0
	}, {
		name: "$mdProgressCircular",
		type: "service",
		outputPath: "partials/api/material.components.progressCircular/service/$mdProgressCircular.html",
		url: "api/service/$mdProgressCircular",
		label: "$mdProgressCircular",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/progressCircular/js/progressCircularProvider.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.components.progressLinear",
	type: "module",
	outputPath: "partials/api/material.components.progressLinear/index.html",
	url: "api/material.components.progressLinear",
	label: "material.components.progressLinear",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/progressLinear/progress-linear.js",
	docs: [{
		name: "mdProgressLinear",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.progressLinear/directive/mdProgressLinear.html",
		url: "api/directive/mdProgressLinear",
		label: "mdProgressLinear",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/progressLinear/progress-linear.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.radioButton",
	type: "module",
	outputPath: "partials/api/material.components.radioButton/index.html",
	url: "api/material.components.radioButton",
	label: "material.components.radioButton",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/radioButton/radio-button.js",
	docs: [{
		name: "mdRadioGroup",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.radioButton/directive/mdRadioGroup.html",
		url: "api/directive/mdRadioGroup",
		label: "mdRadioGroup",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/radioButton/radio-button.js",
		hasDemo: !0
	}, {
		name: "mdRadioButton",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.radioButton/directive/mdRadioButton.html",
		url: "api/directive/mdRadioButton",
		label: "mdRadioButton",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/radioButton/radio-button.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.select",
	type: "module",
	outputPath: "partials/api/material.components.select/index.html",
	url: "api/material.components.select",
	label: "material.components.select",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/select/select.js",
	docs: [{
		name: "mdSelect",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.select/directive/mdSelect.html",
		url: "api/directive/mdSelect",
		label: "mdSelect",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/select/select.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.sidenav",
	type: "module",
	outputPath: "partials/api/material.components.sidenav/index.html",
	url: "api/material.components.sidenav",
	label: "material.components.sidenav",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/sidenav/sidenav.js",
	docs: [{
		name: "$mdSidenav",
		type: "service",
		outputPath: "partials/api/material.components.sidenav/service/$mdSidenav.html",
		url: "api/service/$mdSidenav",
		label: "$mdSidenav",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/sidenav/sidenav.js",
		hasDemo: !1
	}, {
		name: "mdSidenavFocus",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.sidenav/directive/mdSidenavFocus.html",
		url: "api/directive/mdSidenavFocus",
		label: "mdSidenavFocus",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/sidenav/sidenav.js",
		hasDemo: !0
	}, {
		name: "mdSidenav",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.sidenav/directive/mdSidenav.html",
		url: "api/directive/mdSidenav",
		label: "mdSidenav",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/sidenav/sidenav.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.slider",
	type: "module",
	outputPath: "partials/api/material.components.slider/index.html",
	url: "api/material.components.slider",
	label: "material.components.slider",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/slider/slider.js",
	docs: [{
		name: "mdSliderContainer",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.slider/directive/mdSliderContainer.html",
		url: "api/directive/mdSliderContainer",
		label: "mdSliderContainer",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/slider/slider.js",
		hasDemo: !0
	}, {
		name: "mdSlider",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.slider/directive/mdSlider.html",
		url: "api/directive/mdSlider",
		label: "mdSlider",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/slider/slider.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.sticky",
	type: "module",
	outputPath: "partials/api/material.components.sticky/index.html",
	url: "api/material.components.sticky",
	label: "material.components.sticky",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/sticky/sticky.js",
	docs: [{
		name: "$mdSticky",
		type: "service",
		outputPath: "partials/api/material.components.sticky/service/$mdSticky.html",
		url: "api/service/$mdSticky",
		label: "$mdSticky",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/sticky/sticky.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.components.subheader",
	type: "module",
	outputPath: "partials/api/material.components.subheader/index.html",
	url: "api/material.components.subheader",
	label: "material.components.subheader",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/subheader/subheader.js",
	docs: [{
		name: "mdSubheader",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.subheader/directive/mdSubheader.html",
		url: "api/directive/mdSubheader",
		label: "mdSubheader",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/subheader/subheader.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.swipe",
	type: "module",
	outputPath: "partials/api/material.components.swipe/index.html",
	url: "api/material.components.swipe",
	label: "material.components.swipe",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/swipe/swipe.js",
	docs: [{
		name: "mdSwipeLeft",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.swipe/directive/mdSwipeLeft.html",
		url: "api/directive/mdSwipeLeft",
		label: "mdSwipeLeft",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/swipe/swipe.js",
		hasDemo: !0
	}, {
		name: "mdSwipeRight",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.swipe/directive/mdSwipeRight.html",
		url: "api/directive/mdSwipeRight",
		label: "mdSwipeRight",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/swipe/swipe.js",
		hasDemo: !0
	}, {
		name: "mdSwipeUp",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.swipe/directive/mdSwipeUp.html",
		url: "api/directive/mdSwipeUp",
		label: "mdSwipeUp",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/swipe/swipe.js",
		hasDemo: !0
	}, {
		name: "mdSwipeDown",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.swipe/directive/mdSwipeDown.html",
		url: "api/directive/mdSwipeDown",
		label: "mdSwipeDown",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/swipe/swipe.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.switch",
	type: "module",
	outputPath: "partials/api/material.components.switch/index.html",
	url: "api/material.components.switch",
	label: "material.components.switch",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/switch/switch.js",
	docs: [{
		name: "mdSwitch",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.switch/directive/mdSwitch.html",
		url: "api/directive/mdSwitch",
		label: "mdSwitch",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/switch/switch.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.tabs",
	type: "module",
	outputPath: "partials/api/material.components.tabs/index.html",
	url: "api/material.components.tabs",
	label: "material.components.tabs",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/tabs/tabs.js",
	docs: [{
		name: "mdTab",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.tabs/directive/mdTab.html",
		url: "api/directive/mdTab",
		label: "mdTab",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/tabs/js/tabDirective.js",
		hasDemo: !0
	}, {
		name: "mdTabs",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.tabs/directive/mdTabs.html",
		url: "api/directive/mdTabs",
		label: "mdTabs",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/tabs/js/tabsDirective.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.toast",
	type: "module",
	outputPath: "partials/api/material.components.toast/index.html",
	url: "api/material.components.toast",
	label: "material.components.toast",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/toast/toast.js",
	docs: [{
		name: "$mdToast",
		type: "service",
		outputPath: "partials/api/material.components.toast/service/$mdToast.html",
		url: "api/service/$mdToast",
		label: "$mdToast",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/toast/toast.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.components.toolbar",
	type: "module",
	outputPath: "partials/api/material.components.toolbar/index.html",
	url: "api/material.components.toolbar",
	label: "material.components.toolbar",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/toolbar/toolbar.js",
	docs: [{
		name: "mdToolbar",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.toolbar/directive/mdToolbar.html",
		url: "api/directive/mdToolbar",
		label: "mdToolbar",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/toolbar/toolbar.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.tooltip",
	type: "module",
	outputPath: "partials/api/material.components.tooltip/index.html",
	url: "api/material.components.tooltip",
	label: "material.components.tooltip",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/tooltip/tooltip.js",
	docs: [{
		name: "mdTooltip",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.tooltip/directive/mdTooltip.html",
		url: "api/directive/mdTooltip",
		label: "mdTooltip",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/tooltip/tooltip.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.truncate",
	type: "module",
	outputPath: "partials/api/material.components.truncate/index.html",
	url: "api/material.components.truncate",
	label: "material.components.truncate",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/truncate/truncate.js",
	docs: [{
		name: "mdTruncate",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.truncate/directive/mdTruncate.html",
		url: "api/directive/mdTruncate",
		label: "mdTruncate",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/truncate/truncate.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.virtualRepeat",
	type: "module",
	outputPath: "partials/api/material.components.virtualRepeat/index.html",
	url: "api/material.components.virtualRepeat",
	label: "material.components.virtualRepeat",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/virtualRepeat/virtual-repeater.js",
	docs: [{
		name: "mdVirtualRepeatContainer",
		type: "directive",
		restrict: {element: !0, attribute: !1, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.virtualRepeat/directive/mdVirtualRepeatContainer.html",
		url: "api/directive/mdVirtualRepeatContainer",
		label: "mdVirtualRepeatContainer",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/virtualRepeat/virtual-repeater.js",
		hasDemo: !0
	}, {
		name: "mdVirtualRepeat",
		type: "directive",
		restrict: {element: !1, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.virtualRepeat/directive/mdVirtualRepeat.html",
		url: "api/directive/mdVirtualRepeat",
		label: "mdVirtualRepeat",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/virtualRepeat/virtual-repeater.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.components.whiteframe",
	type: "module",
	outputPath: "partials/api/material.components.whiteframe/index.html",
	url: "api/material.components.whiteframe",
	label: "material.components.whiteframe",
	module: "material.components",
	githubUrl: "https://github.com/angular/material/blob/master/src/components/whiteframe/whiteframe.js",
	docs: [{
		name: "mdWhiteframe",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.components.whiteframe/directive/mdWhiteframe.html",
		url: "api/directive/mdWhiteframe",
		label: "mdWhiteframe",
		module: "material.components",
		githubUrl: "https://github.com/angular/material/blob/master/src/components/whiteframe/whiteframe.js",
		hasDemo: !0
	}],
	hasDemo: !1
}, {
	name: "material.core.aria",
	type: "module",
	outputPath: "partials/api/material.core.aria/index.html",
	url: "api/material.core.aria",
	label: "material.core.aria",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/services/aria/aria.js",
	docs: [{
		name: "$mdAriaProvider",
		type: "service",
		outputPath: "partials/api/material.core.aria/service/$mdAriaProvider.html",
		url: "api/service/$mdAriaProvider",
		label: "$mdAriaProvider",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/aria/aria.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.core.compiler",
	type: "module",
	outputPath: "partials/api/material.core.compiler/index.html",
	url: "api/material.core.compiler",
	label: "material.core.compiler",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/services/compiler/compiler.js",
	docs: [{
		name: "$mdCompiler",
		type: "service",
		outputPath: "partials/api/material.core.compiler/service/$mdCompiler.html",
		url: "api/service/$mdCompiler",
		label: "$mdCompiler",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/compiler/compiler.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.core.interaction",
	type: "module",
	outputPath: "partials/api/material.core.interaction/index.html",
	url: "api/material.core.interaction",
	label: "material.core.interaction",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/services/interaction/interaction.js",
	docs: [{
		name: "$mdInteraction",
		type: "service",
		outputPath: "partials/api/material.core.interaction/service/$mdInteraction.html",
		url: "api/service/$mdInteraction",
		label: "$mdInteraction",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/interaction/interaction.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.core.liveannouncer",
	type: "module",
	outputPath: "partials/api/material.core.liveannouncer/index.html",
	url: "api/material.core.liveannouncer",
	label: "material.core.liveannouncer",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/services/liveAnnouncer/live-announcer.js",
	docs: [{
		name: "$mdLiveAnnouncer",
		type: "service",
		outputPath: "partials/api/material.core.liveannouncer/service/$mdLiveAnnouncer.html",
		url: "api/service/$mdLiveAnnouncer",
		label: "$mdLiveAnnouncer",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/liveAnnouncer/live-announcer.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.core.ripple",
	type: "module",
	outputPath: "partials/api/material.core.ripple/index.html",
	url: "api/material.core.ripple",
	label: "material.core.ripple",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/services/ripple/ripple.js",
	docs: [{
		name: "mdInkRipple",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.core.ripple/directive/mdInkRipple.html",
		url: "api/directive/mdInkRipple",
		label: "mdInkRipple",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/ripple/ripple.js",
		hasDemo: !0
	}, {
		name: "$mdInkRipple",
		type: "service",
		outputPath: "partials/api/material.core.ripple/service/$mdInkRipple.html",
		url: "api/service/$mdInkRipple",
		label: "$mdInkRipple",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/ripple/ripple.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.core.theming",
	type: "module",
	outputPath: "partials/api/material.core.theming/index.html",
	url: "api/material.core.theming",
	label: "material.core.theming",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/services/theming/theming.js",
	docs: [{
		name: "$mdThemingProvider",
		type: "service",
		outputPath: "partials/api/material.core.theming/service/$mdThemingProvider.html",
		url: "api/service/$mdThemingProvider",
		label: "$mdThemingProvider",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/theming/theming.js",
		hasDemo: !1
	}, {
		name: "$mdTheming",
		type: "service",
		outputPath: "partials/api/material.core.theming/service/$mdTheming.html",
		url: "api/service/$mdTheming",
		label: "$mdTheming",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/services/theming/theming.js",
		hasDemo: !1
	}],
	hasDemo: !1
}, {
	name: "material.core.util",
	type: "module",
	outputPath: "partials/api/material.core.util/index.html",
	url: "api/material.core.util",
	label: "material.core.util",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/util/util.js",
	docs: [{
		name: "mdAutofocus",
		type: "directive",
		restrict: {element: !0, attribute: !0, cssClass: !1, comment: !1},
		outputPath: "partials/api/material.core.util/directive/mdAutofocus.html",
		url: "api/directive/mdAutofocus",
		label: "mdAutofocus",
		module: "material.core",
		githubUrl: "https://github.com/angular/material/blob/master/src/core/util/autofocus.js",
		hasDemo: !0
	}],
	hasDemo: !1
}]), angular.module("docsApp").constant("PAGES", {
	CSS: [{
		name: "button",
		outputPath: "partials/CSS/button.html",
		url: "/CSS/button",
		label: "button"
	}, {name: "checkbox", outputPath: "partials/CSS/checkbox.html", url: "/CSS/checkbox", label: "checkbox"}, {
		name: "Typography",
		outputPath: "partials/CSS/typography.html",
		url: "/CSS/typography",
		label: "Typography"
	}],
	Theming: [{
		name: "Introduction and Terms",
		outputPath: "partials/Theming/01_introduction.html",
		url: "/Theming/01_introduction",
		label: "Introduction and Terms"
	}, {
		name: "Declarative Syntax",
		outputPath: "partials/Theming/02_declarative_syntax.html",
		url: "/Theming/02_declarative_syntax",
		label: "Declarative Syntax"
	}, {
		name: "Configuring a Theme",
		outputPath: "partials/Theming/03_configuring_a_theme.html",
		url: "/Theming/03_configuring_a_theme",
		label: "Configuring a Theme"
	}, {
		name: "Multiple Themes",
		outputPath: "partials/Theming/04_multiple_themes.html",
		url: "/Theming/04_multiple_themes",
		label: "Multiple Themes"
	}, {
		name: "Theming under the hood",
		outputPath: "partials/Theming/05_under_the_hood.html",
		url: "/Theming/05_under_the_hood",
		label: "Theming under the hood"
	}, {name: "Browser Colors", outputPath: "partials/Theming/06_browser_color.html", url: "/Theming/06_browser_color", label: "Browser Colors"}]
}), function () {
	function e() {
		return {
			restrict: "E",
			transclude: !0,
			bindToController: !0,
			controller: function () {
			},
			controllerAs: "$ctrl",
			scope: {},
			template: '<table class="md-api-table md-css-table">  <thead>    <tr><th>Available Selectors</th></tr>  </thead>  <tbody ng-transclude>  </tbody></table>'
		}
	}

	function t() {
		return {
			restrict: "E",
			transclude: !0,
			replace: !0,
			bindToController: !0,
			controller: function () {
			},
			controllerAs: "$ctrl",
			scope: {code: "@"},
			template: '<tr>  <td>    <code class="md-css-selector">{{$ctrl.code}}</code>    <span ng-transclude></span>  </td></tr>'
		}
	}

	angular.module("docsApp").directive("docsCssApiTable", e).directive("docsCssSelector", t)
}(), angular.module("docsApp").constant("DEMOS", [{
	name: "autocomplete",
	moduleName: "material.components.autocomplete",
	label: "Autocomplete",
	demos: [{
		ngModule: {name: "autocompleteDemo", module: "autocompleteDemo", dependencies: ["ngMaterial"]},
		id: "autocompletedemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/autocomplete/demoBasicUsage/script.js"}],
		moduleName: "material.components.autocomplete",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/autocomplete/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "autocompleteCustomTemplateDemo", module: "autocompleteCustomTemplateDemo", dependencies: ["ngMaterial"]},
		id: "autocompletedemoCustomTemplate",
		css: [{
			name: "style.global.css",
			label: "style.global.css",
			fileType: "css",
			outputPath: "demo-partials/autocomplete/demoCustomTemplate/style.global.css"
		}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/autocomplete/demoCustomTemplate/script.js"}],
		moduleName: "material.components.autocomplete",
		name: "demoCustomTemplate",
		label: "Custom Template",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/autocomplete/demoCustomTemplate/index.html"}
	}, {
		ngModule: {name: "autocompleteFloatingLabelDemo", module: "autocompleteFloatingLabelDemo", dependencies: ["ngMaterial", "ngMessages"]},
		id: "autocompletedemoFloatingLabel",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/autocomplete/demoFloatingLabel/script.js"}],
		moduleName: "material.components.autocomplete",
		name: "demoFloatingLabel",
		label: "Floating Label",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/autocomplete/demoFloatingLabel/index.html"}
	}, {
		ngModule: {name: "autocompleteDemoInsideDialog", module: "autocompleteDemoInsideDialog", dependencies: ["ngMaterial"]},
		id: "autocompletedemoInsideDialog",
		css: [],
		html: [{
			name: "dialog.tmpl.html",
			label: "dialog.tmpl.html",
			fileType: "html",
			outputPath: "demo-partials/autocomplete/demoInsideDialog/dialog.tmpl.html"
		}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/autocomplete/demoInsideDialog/script.js"}],
		moduleName: "material.components.autocomplete",
		name: "demoInsideDialog",
		label: "Inside Dialog",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/autocomplete/demoInsideDialog/index.html"}
	}],
	url: "demo/autocomplete"
}, {
	name: "bottomSheet",
	moduleName: "material.components.bottomSheet",
	label: "Bottom Sheet",
	demos: [{
		ngModule: {name: "bottomSheetDemo1", module: "bottomSheetDemo1", dependencies: ["ngMaterial"]},
		id: "bottomSheetdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/bottomSheet/demoBasicUsage/style.css"}],
		html: [{
			name: "bottom-sheet-grid-template.html",
			label: "bottom-sheet-grid-template.html",
			fileType: "html",
			outputPath: "demo-partials/bottomSheet/demoBasicUsage/bottom-sheet-grid-template.html"
		}, {
			name: "bottom-sheet-list-template.html",
			label: "bottom-sheet-list-template.html",
			fileType: "html",
			outputPath: "demo-partials/bottomSheet/demoBasicUsage/bottom-sheet-list-template.html"
		}, {name: "readme.html", label: "readme.html", fileType: "html", outputPath: "demo-partials/bottomSheet/demoBasicUsage/readme.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/bottomSheet/demoBasicUsage/script.js"}],
		moduleName: "material.components.bottomSheet",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/bottomSheet/demoBasicUsage/index.html"}
	}],
	url: "demo/bottomSheet"
}, {
	name: "button", moduleName: "material.components.button", label: "Button", demos: [{
		ngModule: {name: "buttonsDemo1", module: "buttonsDemo1", dependencies: ["ngMaterial"]},
		id: "buttondemoBasicUsage",
		css: [{
			name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/button/demoBasicUsage/style.css"
		}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/button/demoBasicUsage/script.js"}],
		moduleName: "material.components.button",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/button/demoBasicUsage/index.html"}
	}], url: "demo/button"
}, {
	name: "card",
	moduleName: "material.components.card",
	label: "Card",
	demos: [{
		ngModule: {name: "cardDemo1", module: "cardDemo1", dependencies: ["ngMaterial"]},
		id: "carddemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/card/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/card/demoBasicUsage/script.js"}],
		moduleName: "material.components.card",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/card/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "cardDemo2", module: "cardDemo2", dependencies: ["ngMaterial"]},
		id: "carddemoCardActionButtons",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/card/demoCardActionButtons/script.js"}],
		moduleName: "material.components.card",
		name: "demoCardActionButtons",
		label: "Card Action Buttons",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/card/demoCardActionButtons/index.html"}
	}, {
		ngModule: {name: "cardDemo3", module: "cardDemo3", dependencies: ["ngMaterial"]},
		id: "carddemoInCardActions",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/card/demoInCardActions/script.js"}],
		moduleName: "material.components.card",
		name: "demoInCardActions",
		label: "In Card Actions",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/card/demoInCardActions/index.html"}
	}],
	url: "demo/card"
}, {
	name: "checkbox",
	moduleName: "material.components.checkbox",
	label: "Checkbox",
	demos: [{
		ngModule: {name: "checkboxDemo1", module: "checkboxDemo1", dependencies: ["ngMaterial"]},
		id: "checkboxdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/checkbox/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/checkbox/demoBasicUsage/script.js"}],
		moduleName: "material.components.checkbox",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/checkbox/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "checkboxDemo3", module: "checkboxDemo3", dependencies: ["ngMaterial"]},
		id: "checkboxdemoSelectAll",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/checkbox/demoSelectAll/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/checkbox/demoSelectAll/script.js"}],
		moduleName: "material.components.checkbox",
		name: "demoSelectAll",
		label: "Select All",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/checkbox/demoSelectAll/index.html"}
	}, {
		ngModule: {name: "checkboxDemo2", module: "checkboxDemo2", dependencies: ["ngMaterial"]},
		id: "checkboxdemoSyncing",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/checkbox/demoSyncing/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/checkbox/demoSyncing/script.js"}],
		moduleName: "material.components.checkbox",
		name: "demoSyncing",
		label: "Syncing",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/checkbox/demoSyncing/index.html"}
	}],
	url: "demo/checkbox"
}, {
	name: "chips",
	moduleName: "material.components.chips",
	label: "Chips",
	demos: [{
		ngModule: {name: "chipsDemo", module: "chipsDemo", dependencies: ["ngMaterial", "ngMessages"]},
		id: "chipsdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/chips/demoBasicUsage/style.css"}],
		html: [{name: "readme.html", label: "readme.html", fileType: "html", outputPath: "demo-partials/chips/demoBasicUsage/readme.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/chips/demoBasicUsage/script.js"}],
		moduleName: "material.components.chips",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/chips/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "contactChipsDemo", module: "contactChipsDemo", dependencies: ["ngMaterial"]},
		id: "chipsdemoContactChips",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/chips/demoContactChips/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/chips/demoContactChips/script.js"}],
		moduleName: "material.components.chips",
		name: "demoContactChips",
		label: "Contact Chips",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/chips/demoContactChips/index.html"}
	}, {
		ngModule: {name: "chipsCustomInputDemo", module: "chipsCustomInputDemo", dependencies: ["ngMaterial"]},
		id: "chipsdemoCustomInputs",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/chips/demoCustomInputs/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/chips/demoCustomInputs/script.js"}],
		moduleName: "material.components.chips",
		name: "demoCustomInputs",
		label: "Custom Inputs",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/chips/demoCustomInputs/index.html"}
	}, {
		ngModule: {name: "chipsCustomSeparatorDemo", module: "chipsCustomSeparatorDemo", dependencies: ["ngMaterial"]},
		id: "chipsdemoCustomSeparatorKeys",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/chips/demoCustomSeparatorKeys/script.js"}],
		moduleName: "material.components.chips",
		name: "demoCustomSeparatorKeys",
		label: "Custom Separator Keys",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/chips/demoCustomSeparatorKeys/index.html"}
	}, {
		ngModule: {name: "staticChipsDemo", module: "staticChipsDemo", dependencies: ["ngMaterial"]},
		id: "chipsdemoStaticChips",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/chips/demoStaticChips/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/chips/demoStaticChips/script.js"}],
		moduleName: "material.components.chips",
		name: "demoStaticChips",
		label: "Static Chips",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/chips/demoStaticChips/index.html"}
	}],
	url: "demo/chips"
}, {
	name: "colors",
	moduleName: "material.components.colors",
	label: "Colors",
	demos: [{
		ngModule: {name: "colorsDemo", module: "colorsDemo", dependencies: ["ngMaterial"]},
		id: "colorsdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/colors/demoBasicUsage/style.css"}],
		html: [{
			name: "regularCard.tmpl.html",
			label: "regularCard.tmpl.html",
			fileType: "html",
			outputPath: "demo-partials/colors/demoBasicUsage/regularCard.tmpl.html"
		}, {name: "userCard.tmpl.html", label: "userCard.tmpl.html", fileType: "html", outputPath: "demo-partials/colors/demoBasicUsage/userCard.tmpl.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/colors/demoBasicUsage/script.js"}],
		moduleName: "material.components.colors",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/colors/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "colorsThemePickerDemo", module: "colorsThemePickerDemo", dependencies: ["ngMaterial"]},
		id: "colorsdemoThemePicker",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/colors/demoThemePicker/style.css"}],
		html: [{
			name: "themePreview.tmpl.html",
			label: "themePreview.tmpl.html",
			fileType: "html",
			outputPath: "demo-partials/colors/demoThemePicker/themePreview.tmpl.html"
		}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/colors/demoThemePicker/script.js"}],
		moduleName: "material.components.colors",
		name: "demoThemePicker",
		label: "Theme Picker",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/colors/demoThemePicker/index.html"}
	}],
	url: "demo/colors"
}, {
	name: "content",
	moduleName: "material.components.content",
	label: "Content",
	demos: [{
		ngModule: {name: "contentDemo1", module: "contentDemo1", dependencies: ["ngMaterial"]},
		id: "contentdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/content/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/content/demoBasicUsage/script.js"}],
		moduleName: "material.components.content",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/content/demoBasicUsage/index.html"}
	}],
	url: "demo/content"
}, {
	name: "datepicker",
	moduleName: "material.components.datepicker",
	label: "Datepicker",
	demos: [{
		ngModule: {name: "datepickerBasicUsage", module: "datepickerBasicUsage", dependencies: ["ngMaterial", "ngMessages"]},
		id: "datepickerdemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/datepicker/demoBasicUsage/script.js"}],
		moduleName: "material.components.datepicker",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/datepicker/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "datepickerValidations", module: "datepickerValidations", dependencies: ["ngMaterial", "ngMessages"]},
		id: "datepickerdemoValidations",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/datepicker/demoValidations/script.js"}],
		moduleName: "material.components.datepicker",
		name: "demoValidations",
		label: "Validations",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/datepicker/demoValidations/index.html"}
	}],
	url: "demo/datepicker"
}, {
	name: "dialog",
	moduleName: "material.components.dialog",
	label: "Dialog",
	demos: [{
		ngModule: {name: "dialogDemo1", module: "dialogDemo1", dependencies: ["ngMaterial"]},
		id: "dialogdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/dialog/demoBasicUsage/style.css"}],
		html: [{
			name: "dialog1.tmpl.html",
			label: "dialog1.tmpl.html",
			fileType: "html",
			outputPath: "demo-partials/dialog/demoBasicUsage/dialog1.tmpl.html"
		}, {
			name: "tabDialog.tmpl.html",
			label: "tabDialog.tmpl.html",
			fileType: "html",
			outputPath: "demo-partials/dialog/demoBasicUsage/tabDialog.tmpl.html"
		}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/dialog/demoBasicUsage/script.js"}],
		moduleName: "material.components.dialog",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/dialog/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "dialogDemo2", module: "dialogDemo2", dependencies: ["ngMaterial"]},
		id: "dialogdemoOpenFromCloseTo",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/dialog/demoOpenFromCloseTo/script.js"}],
		moduleName: "material.components.dialog",
		name: "demoOpenFromCloseTo",
		label: "Open From Close To",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/dialog/demoOpenFromCloseTo/index.html"}
	}, {
		ngModule: {name: "dialogDemo3", module: "dialogDemo3", dependencies: ["ngMaterial"]},
		id: "dialogdemoThemeInheritance",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/dialog/demoThemeInheritance/style.css"}],
		html: [{
			name: "dialog1.tmpl.html",
			label: "dialog1.tmpl.html",
			fileType: "html",
			outputPath: "demo-partials/dialog/demoThemeInheritance/dialog1.tmpl.html"
		}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/dialog/demoThemeInheritance/script.js"}],
		moduleName: "material.components.dialog",
		name: "demoThemeInheritance",
		label: "Theme Inheritance",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/dialog/demoThemeInheritance/index.html"}
	}],
	url: "demo/dialog"
}, {
	name: "divider",
	moduleName: "material.components.divider",
	label: "Divider",
	demos: [{
		ngModule: {name: "dividerDemo1", module: "dividerDemo1", dependencies: ["ngMaterial"]},
		id: "dividerdemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/divider/demoBasicUsage/script.js"}],
		moduleName: "material.components.divider",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/divider/demoBasicUsage/index.html"}
	}],
	url: "demo/divider"
}, {
	name: "fabSpeedDial",
	moduleName: "material.components.fabSpeedDial",
	label: "FAB Speed Dial",
	demos: [{
		ngModule: {name: "fabSpeedDialDemoBasicUsage", module: "fabSpeedDialDemoBasicUsage", dependencies: ["ngMaterial"]},
		id: "fabSpeedDialdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/fabSpeedDial/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/fabSpeedDial/demoBasicUsage/script.js"}],
		moduleName: "material.components.fabSpeedDial",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/fabSpeedDial/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "fabSpeedDialDemoMoreOptions", module: "fabSpeedDialDemoMoreOptions", dependencies: ["ngMaterial"]},
		id: "fabSpeedDialdemoMoreOptions",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/fabSpeedDial/demoMoreOptions/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/fabSpeedDial/demoMoreOptions/script.js"}],
		moduleName: "material.components.fabSpeedDial",
		name: "demoMoreOptions",
		label: "More Options",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/fabSpeedDial/demoMoreOptions/index.html"}
	}],
	url: "demo/fabSpeedDial"
}, {
	name: "fabToolbar",
	moduleName: "material.components.fabToolbar",
	label: "FAB Toolbar",
	demos: [{
		ngModule: {name: "fabToolbarBasicUsageDemo", module: "fabToolbarBasicUsageDemo", dependencies: ["ngMaterial"]},
		id: "fabToolbardemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/fabToolbar/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/fabToolbar/demoBasicUsage/script.js"}],
		moduleName: "material.components.fabToolbar",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/fabToolbar/demoBasicUsage/index.html"}
	}],
	url: "demo/fabToolbar"
}, {
	name: "gridList",
	moduleName: "material.components.gridList",
	label: "Grid List",
	demos: [{
		ngModule: {name: "gridListDemo1", module: "gridListDemo1", dependencies: ["ngMaterial"]},
		id: "gridListdemoBasicUsage",
		css: [{name: "styles.css", label: "styles.css", fileType: "css", outputPath: "demo-partials/gridList/demoBasicUsage/styles.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/gridList/demoBasicUsage/script.js"}],
		moduleName: "material.components.gridList",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/gridList/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "gridListDemoApp", module: "gridListDemoApp", dependencies: ["ngMaterial"]},
		id: "gridListdemoDynamicTiles",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/gridList/demoDynamicTiles/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/gridList/demoDynamicTiles/script.js"}],
		moduleName: "material.components.gridList",
		name: "demoDynamicTiles",
		label: "Dynamic Tiles",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/gridList/demoDynamicTiles/index.html"}
	}, {
		ngModule: {name: "gridListDemo1", module: "gridListDemo1", dependencies: ["ngMaterial"]},
		id: "gridListdemoResponsiveUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/gridList/demoResponsiveUsage/script.js"}],
		moduleName: "material.components.gridList",
		name: "demoResponsiveUsage",
		label: "Responsive Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/gridList/demoResponsiveUsage/index.html"}
	}],
	url: "demo/gridList"
}, {
	name: "icon",
	moduleName: "material.components.icon",
	label: "Icon",
	demos: [{
		ngModule: {name: "appDemoFontIconsWithClassnames", module: "appDemoFontIconsWithClassnames", dependencies: ["ngMaterial"]},
		id: "icondemoFontIconsWithClassnames",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/icon/demoFontIconsWithClassnames/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/icon/demoFontIconsWithClassnames/script.js"}],
		moduleName: "material.components.icon",
		name: "demoFontIconsWithClassnames",
		label: "Font Icons With Classnames",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/icon/demoFontIconsWithClassnames/index.html"}
	}, {
		ngModule: {name: "appDemoFontIconsWithLigatures", module: "appDemoFontIconsWithLigatures", dependencies: ["ngMaterial"]},
		id: "icondemoFontIconsWithLigatures",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/icon/demoFontIconsWithLigatures/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/icon/demoFontIconsWithLigatures/script.js"}],
		moduleName: "material.components.icon",
		name: "demoFontIconsWithLigatures",
		label: "Font Icons With Ligatures",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/icon/demoFontIconsWithLigatures/index.html"}
	}, {
		ngModule: {name: "appDemoSvgIcons", module: "appDemoSvgIcons", dependencies: ["ngMaterial"]},
		id: "icondemoLoadSvgIconsFromUrl",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/icon/demoLoadSvgIconsFromUrl/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/icon/demoLoadSvgIconsFromUrl/script.js"}],
		moduleName: "material.components.icon",
		name: "demoLoadSvgIconsFromUrl",
		label: "Load Svg Icons From Url",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/icon/demoLoadSvgIconsFromUrl/index.html"}
	}, {
		ngModule: {name: "appSvgIconSets", module: "appSvgIconSets", dependencies: ["ngMaterial"]},
		id: "icondemoSvgIconSets",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/icon/demoSvgIconSets/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/icon/demoSvgIconSets/script.js"}],
		moduleName: "material.components.icon",
		name: "demoSvgIconSets",
		label: "Svg Icon Sets",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/icon/demoSvgIconSets/index.html"}
	}, {
		ngModule: {name: "appUsingTemplateCache", module: "appUsingTemplateCache", dependencies: ["ngMaterial"]},
		id: "icondemoUsingTemplateRequest",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/icon/demoUsingTemplateRequest/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/icon/demoUsingTemplateRequest/script.js"}],
		moduleName: "material.components.icon",
		name: "demoUsingTemplateRequest",
		label: "Using Template Request",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/icon/demoUsingTemplateRequest/index.html"}
	}],
	url: "demo/icon"
}, {
	name: "input",
	moduleName: "material.components.input",
	label: "Input",
	demos: [{
		ngModule: {name: "inputBasicDemo", module: "inputBasicDemo", dependencies: ["ngMaterial", "ngMessages"]},
		id: "inputdemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/input/demoBasicUsage/script.js"}],
		moduleName: "material.components.input",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/input/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "inputErrorsApp", module: "inputErrorsApp", dependencies: ["ngMaterial", "ngMessages"]},
		id: "inputdemoErrors",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/input/demoErrors/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/input/demoErrors/script.js"}],
		moduleName: "material.components.input",
		name: "demoErrors",
		label: "Errors",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/input/demoErrors/index.html"}
	}, {
		ngModule: {name: "inputErrorsAdvancedApp", module: "inputErrorsAdvancedApp", dependencies: ["ngMaterial", "ngMessages"]},
		id: "inputdemoErrorsAdvanced",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/input/demoErrorsAdvanced/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/input/demoErrorsAdvanced/script.js"}],
		moduleName: "material.components.input",
		name: "demoErrorsAdvanced",
		label: "Errors Advanced",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/input/demoErrorsAdvanced/index.html"}
	}, {
		ngModule: {name: "inputIconDemo", module: "inputIconDemo", dependencies: ["ngMaterial", "ngMessages"]},
		id: "inputdemoIcons",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/input/demoIcons/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/input/demoIcons/script.js"}],
		moduleName: "material.components.input",
		name: "demoIcons",
		label: "Icons",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/input/demoIcons/index.html"}
	}],
	url: "demo/input"
}, {
	name: "list",
	moduleName: "material.components.list",
	label: "List",
	demos: [{
		ngModule: {name: "listDemo1", module: "listDemo1", dependencies: ["ngMaterial"]},
		id: "listdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/list/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/list/demoBasicUsage/script.js"}],
		moduleName: "material.components.list",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/list/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "listDemo2", module: "listDemo2", dependencies: ["ngMaterial"]},
		id: "listdemoListControls",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/list/demoListControls/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/list/demoListControls/script.js"}],
		moduleName: "material.components.list",
		name: "demoListControls",
		label: "List Controls",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/list/demoListControls/index.html"}
	}],
	url: "demo/list"
}, {
	name: "menu",
	moduleName: "material.components.menu",
	label: "Menu",
	demos: [{
		ngModule: {name: "menuDemoBasic", module: "menuDemoBasic", dependencies: ["ngMaterial"]},
		id: "menudemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/menu/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/menu/demoBasicUsage/script.js"}],
		moduleName: "material.components.menu",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/menu/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "menuDemoCustomTrigger", module: "menuDemoCustomTrigger", dependencies: ["ngMaterial"]},
		id: "menudemoCustomTrigger",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/menu/demoCustomTrigger/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/menu/demoCustomTrigger/script.js"}],
		moduleName: "material.components.menu",
		name: "demoCustomTrigger",
		label: "Custom Trigger",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/menu/demoCustomTrigger/index.html"}
	}, {
		ngModule: {name: "menuDemoPosition", module: "menuDemoPosition", dependencies: ["ngMaterial"]},
		id: "menudemoMenuPositionModes",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/menu/demoMenuPositionModes/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/menu/demoMenuPositionModes/script.js"}],
		moduleName: "material.components.menu",
		name: "demoMenuPositionModes",
		label: "Menu Position Modes",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/menu/demoMenuPositionModes/index.html"}
	}, {
		ngModule: {name: "menuDemoWidth", module: "menuDemoWidth", dependencies: ["ngMaterial"]},
		id: "menudemoMenuWidth",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/menu/demoMenuWidth/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/menu/demoMenuWidth/script.js"}],
		moduleName: "material.components.menu",
		name: "demoMenuWidth",
		label: "Menu Width",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/menu/demoMenuWidth/index.html"}
	}],
	url: "demo/menu"
}, {
	name: "menuBar",
	moduleName: "material.components.menuBar",
	label: "Menu Bar",
	demos: [{
		ngModule: {name: "menuBarDemoBasic", module: "menuBarDemoBasic", dependencies: ["ngMaterial"]},
		id: "menuBardemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/menuBar/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/menuBar/demoBasicUsage/script.js"}],
		moduleName: "material.components.menuBar",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/menuBar/demoBasicUsage/index.html"}
	}],
	url: "demo/menuBar"
}, {
	name: "navBar",
	moduleName: "material.components.navBar",
	label: "Nav Bar",
	demos: [{
		ngModule: {name: "navBarDemoBasicUsage", module: "navBarDemoBasicUsage", dependencies: ["ngMaterial"]},
		id: "navBardemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/navBar/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/navBar/demoBasicUsage/script.js"}],
		moduleName: "material.components.navBar",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/navBar/demoBasicUsage/index.html"}
	}],
	url: "demo/navBar"
}, {
	name: "panel",
	moduleName: "material.components.panel",
	label: "Panel",
	demos: [{
		ngModule: {name: "panelDemo", module: "panelDemo", dependencies: ["ngMaterial"]},
		id: "paneldemoBasicUsage",
		css: [{name: "style.global.css", label: "style.global.css", fileType: "css", outputPath: "demo-partials/panel/demoBasicUsage/style.global.css"}],
		html: [{name: "panel.tmpl.html", label: "panel.tmpl.html", fileType: "html", outputPath: "demo-partials/panel/demoBasicUsage/panel.tmpl.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/panel/demoBasicUsage/script.js"}],
		moduleName: "material.components.panel",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/panel/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "panelGroupsDemo", module: "panelGroupsDemo", dependencies: ["ngMaterial"]},
		id: "paneldemoGroups",
		css: [{name: "style.global.css", label: "style.global.css", fileType: "css", outputPath: "demo-partials/panel/demoGroups/style.global.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/panel/demoGroups/script.js"}],
		moduleName: "material.components.panel",
		name: "demoGroups",
		label: "Groups",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/panel/demoGroups/index.html"}
	}, {
		ngModule: {name: "panelAnimationsDemo", module: "panelAnimationsDemo", dependencies: ["ngMaterial"]},
		id: "paneldemoPanelAnimations",
		css: [{name: "style.global.css", label: "style.global.css", fileType: "css", outputPath: "demo-partials/panel/demoPanelAnimations/style.global.css"}],
		html: [{name: "panel.tmpl.html", label: "panel.tmpl.html", fileType: "html", outputPath: "demo-partials/panel/demoPanelAnimations/panel.tmpl.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/panel/demoPanelAnimations/script.js"}],
		moduleName: "material.components.panel",
		name: "demoPanelAnimations",
		label: "Panel Animations",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/panel/demoPanelAnimations/index.html"}
	}, {
		ngModule: {name: "panelProviderDemo", module: "panelProviderDemo", dependencies: ["ngMaterial"]},
		id: "paneldemoPanelProvider",
		css: [{name: "style.global.css", label: "style.global.css", fileType: "css", outputPath: "demo-partials/panel/demoPanelProvider/style.global.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/panel/demoPanelProvider/script.js"}],
		moduleName: "material.components.panel",
		name: "demoPanelProvider",
		label: "Panel Provider",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/panel/demoPanelProvider/index.html"}
	}],
	url: "demo/panel"
}, {
	name: "progressCircular",
	moduleName: "material.components.progressCircular",
	label: "Progress Circular",
	demos: [{
		ngModule: {name: "progressCircularDemo1", module: "progressCircularDemo1", dependencies: ["ngMaterial"]},
		id: "progressCirculardemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/progressCircular/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/progressCircular/demoBasicUsage/script.js"}],
		moduleName: "material.components.progressCircular",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/progressCircular/demoBasicUsage/index.html"}
	}],
	url: "demo/progressCircular"
}, {
	name: "progressLinear",
	moduleName: "material.components.progressLinear",
	label: "Progress Linear",
	demos: [{
		ngModule: {name: "progressLinearDemo1", module: "progressLinearDemo1", dependencies: ["ngMaterial"]},
		id: "progressLineardemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/progressLinear/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/progressLinear/demoBasicUsage/script.js"}],
		moduleName: "material.components.progressLinear",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/progressLinear/demoBasicUsage/index.html"}
	}],
	url: "demo/progressLinear"
}, {
	name: "radioButton",
	moduleName: "material.components.radioButton",
	label: "Radio Button",
	demos: [{
		ngModule: {name: "radioDemo1", module: "radioDemo1", dependencies: ["ngMaterial"]},
		id: "radioButtondemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/radioButton/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/radioButton/demoBasicUsage/script.js"}],
		moduleName: "material.components.radioButton",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/radioButton/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "radioDemo2", module: "radioDemo2", dependencies: ["ngMaterial"]},
		id: "radioButtondemoMultiColumn",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/radioButton/demoMultiColumn/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/radioButton/demoMultiColumn/script.js"}],
		moduleName: "material.components.radioButton",
		name: "demoMultiColumn",
		label: "Multi Column",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/radioButton/demoMultiColumn/index.html"}
	}],
	url: "demo/radioButton"
}, {
	name: "select",
	moduleName: "material.components.select",
	label: "Select",
	demos: [{
		ngModule: {name: "selectDemoBasic", module: "selectDemoBasic", dependencies: ["ngMaterial"]},
		id: "selectdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/select/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/select/demoBasicUsage/script.js"}],
		moduleName: "material.components.select",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/select/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "selectDemoOptGroups", module: "selectDemoOptGroups", dependencies: ["ngMaterial"]},
		id: "selectdemoOptionGroups",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/select/demoOptionGroups/script.js"}],
		moduleName: "material.components.select",
		name: "demoOptionGroups",
		label: "Option Groups",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/select/demoOptionGroups/index.html"}
	}, {
		ngModule: {name: "selectDemoOptionsAsync", module: "selectDemoOptionsAsync", dependencies: ["ngMaterial"]},
		id: "selectdemoOptionsWithAsyncSearch",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/select/demoOptionsWithAsyncSearch/script.js"}],
		moduleName: "material.components.select",
		name: "demoOptionsWithAsyncSearch",
		label: "Options With Async Search",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/select/demoOptionsWithAsyncSearch/index.html"}
	}, {
		ngModule: {name: "selectDemoSelectHeader", module: "selectDemoSelectHeader", dependencies: ["ngMaterial"]},
		id: "selectdemoSelectHeader",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/select/demoSelectHeader/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/select/demoSelectHeader/script.js"}],
		moduleName: "material.components.select",
		name: "demoSelectHeader",
		label: "Select Header",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/select/demoSelectHeader/index.html"}
	}, {
		ngModule: {name: "selectDemoSelectedText", module: "selectDemoSelectedText", dependencies: ["ngMaterial"]},
		id: "selectdemoSelectedText",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/select/demoSelectedText/script.js"}],
		moduleName: "material.components.select",
		name: "demoSelectedText",
		label: "Selected Text",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/select/demoSelectedText/index.html"}
	}, {
		ngModule: {name: "selectDemoValidation", module: "selectDemoValidation", dependencies: ["ngMaterial", "ngMessages"]},
		id: "selectdemoValidations",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/select/demoValidations/script.js"}],
		moduleName: "material.components.select",
		name: "demoValidations",
		label: "Validations",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/select/demoValidations/index.html"}
	}],
	url: "demo/select"
}, {
	name: "sidenav",
	moduleName: "material.components.sidenav",
	label: "Sidenav",
	demos: [{
		ngModule: {name: "sidenavDemo1", module: "sidenavDemo1", dependencies: ["ngMaterial"]},
		id: "sidenavdemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/sidenav/demoBasicUsage/script.js"}],
		moduleName: "material.components.sidenav",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/sidenav/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "sidenavDemo2", module: "sidenavDemo2", dependencies: ["ngMaterial"]},
		id: "sidenavdemoCustomSidenav",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/sidenav/demoCustomSidenav/script.js"}],
		moduleName: "material.components.sidenav",
		name: "demoCustomSidenav",
		label: "Custom Sidenav",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/sidenav/demoCustomSidenav/index.html"}
	}],
	url: "demo/sidenav"
}, {
	name: "slider",
	moduleName: "material.components.slider",
	label: "Slider",
	demos: [{
		ngModule: {name: "sliderDemo1", module: "sliderDemo1", dependencies: ["ngMaterial"]},
		id: "sliderdemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/slider/demoBasicUsage/script.js"}],
		moduleName: "material.components.slider",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/slider/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "sliderDemo2", module: "sliderDemo2", dependencies: ["ngMaterial"]},
		id: "sliderdemoVertical",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/slider/demoVertical/script.js"}],
		moduleName: "material.components.slider",
		name: "demoVertical",
		label: "Vertical",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/slider/demoVertical/index.html"}
	}],
	url: "demo/slider"
}, {
	name: "subheader",
	moduleName: "material.components.subheader",
	label: "Subheader",
	demos: [{
		ngModule: {name: "subheaderBasicDemo", module: "subheaderBasicDemo", dependencies: ["ngMaterial"]},
		id: "subheaderdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/subheader/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/subheader/demoBasicUsage/script.js"}],
		moduleName: "material.components.subheader",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/subheader/demoBasicUsage/index.html"}
	}],
	url: "demo/subheader"
}, {
	name: "swipe",
	moduleName: "material.components.swipe",
	label: "Swipe",
	demos: [{
		ngModule: {name: "demoSwipe", module: "demoSwipe", dependencies: ["ngMaterial"]},
		id: "swipedemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/swipe/demoBasicUsage/style.css"}],
		html: [{name: "readme.html", label: "readme.html", fileType: "html", outputPath: "demo-partials/swipe/demoBasicUsage/readme.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/swipe/demoBasicUsage/script.js"}],
		moduleName: "material.components.swipe",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/swipe/demoBasicUsage/index.html"}
	}],
	url: "demo/swipe"
}, {
	name: "switch",
	moduleName: "material.components.switch",
	label: "Switch",
	demos: [{
		ngModule: {name: "switchDemo1", module: "switchDemo1", dependencies: ["ngMaterial"]},
		id: "switchdemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/switch/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/switch/demoBasicUsage/script.js"}],
		moduleName: "material.components.switch",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/switch/demoBasicUsage/index.html"}
	}],
	url: "demo/switch"
}, {
	name: "tabs",
	moduleName: "material.components.tabs",
	label: "Tabs",
	demos: [{
		ngModule: {name: "tabsDemoDynamicHeight", module: "tabsDemoDynamicHeight", dependencies: ["ngMaterial"]},
		id: "tabsdemoDynamicHeight",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/tabs/demoDynamicHeight/style.css"}],
		html: [{name: "readme.html", label: "readme.html", fileType: "html", outputPath: "demo-partials/tabs/demoDynamicHeight/readme.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/tabs/demoDynamicHeight/script.js"}],
		moduleName: "material.components.tabs",
		name: "demoDynamicHeight",
		label: "Dynamic Height",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/tabs/demoDynamicHeight/index.html"}
	}, {
		ngModule: {name: "tabsDemoDynamicTabs", module: "tabsDemoDynamicTabs", dependencies: ["ngMaterial"]},
		id: "tabsdemoDynamicTabs",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/tabs/demoDynamicTabs/style.css"}],
		html: [{name: "readme.html", label: "readme.html", fileType: "html", outputPath: "demo-partials/tabs/demoDynamicTabs/readme.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/tabs/demoDynamicTabs/script.js"}],
		moduleName: "material.components.tabs",
		name: "demoDynamicTabs",
		label: "Dynamic Tabs",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/tabs/demoDynamicTabs/index.html"}
	}, {
		ngModule: {name: "tabsDemoIconTabs", module: "tabsDemoIconTabs", dependencies: ["ngMaterial"]},
		id: "tabsdemoStaticTabs",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/tabs/demoStaticTabs/style.css"}],
		html: [{name: "readme.html", label: "readme.html", fileType: "html", outputPath: "demo-partials/tabs/demoStaticTabs/readme.html"}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/tabs/demoStaticTabs/script.js"}],
		moduleName: "material.components.tabs",
		name: "demoStaticTabs",
		label: "Static Tabs",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/tabs/demoStaticTabs/index.html"}
	}],
	url: "demo/tabs"
}, {
	name: "toast",
	moduleName: "material.components.toast",
	label: "Toast",
	demos: [{
		ngModule: {name: "toastDemo1", module: "toastDemo1", dependencies: ["ngMaterial"]},
		id: "toastdemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/toast/demoBasicUsage/script.js"}],
		moduleName: "material.components.toast",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/toast/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "toastDemo2", module: "toastDemo2", dependencies: ["ngMaterial"]},
		id: "toastdemoCustomUsage",
		css: [],
		html: [{
			name: "toast-template.html",
			label: "toast-template.html",
			fileType: "html",
			outputPath: "demo-partials/toast/demoCustomUsage/toast-template.html"
		}],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/toast/demoCustomUsage/script.js"}],
		moduleName: "material.components.toast",
		name: "demoCustomUsage",
		label: "Custom Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/toast/demoCustomUsage/index.html"}
	}],
	url: "demo/toast"
}, {
	name: "toolbar",
	moduleName: "material.components.toolbar",
	label: "Toolbar",
	demos: [{
		ngModule: {name: "toolbarDemo1", module: "toolbarDemo1", dependencies: ["ngMaterial"]},
		id: "toolbardemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/toolbar/demoBasicUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/toolbar/demoBasicUsage/script.js"}],
		moduleName: "material.components.toolbar",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/toolbar/demoBasicUsage/index.html"}
	}, {
		ngModule: {name: "toolbarDemo2", module: "toolbarDemo2", dependencies: ["ngMaterial"]},
		id: "toolbardemoScrollShrink",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/toolbar/demoScrollShrink/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/toolbar/demoScrollShrink/script.js"}],
		moduleName: "material.components.toolbar",
		name: "demoScrollShrink",
		label: "Scroll Shrink",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/toolbar/demoScrollShrink/index.html"}
	}],
	url: "demo/toolbar"
}, {
	name: "tooltip",
	moduleName: "material.components.tooltip",
	label: "Tooltip",
	demos: [{
		ngModule: {name: "tooltipDemo", module: "tooltipDemo", dependencies: ["ngMaterial"]},
		id: "tooltipdemoBasicUsage",
		css: [],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/tooltip/demoBasicUsage/script.js"}],
		moduleName: "material.components.tooltip",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/tooltip/demoBasicUsage/index.html"}
	}],
	url: "demo/tooltip"
}, {
	name: "truncate",
	moduleName: "material.components.truncate",
	label: "Truncate",
	demos: [{
		ngModule: "",
		id: "truncatedemoBasicUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/truncate/demoBasicUsage/style.css"}],
		html: [],
		js: [],
		moduleName: "material.components.truncate",
		name: "demoBasicUsage",
		label: "Basic Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/truncate/demoBasicUsage/index.html"}
	}],
	url: "demo/truncate"
}, {
	name: "virtualRepeat",
	moduleName: "material.components.virtualRepeat",
	label: "Virtual Repeat",
	demos: [{
		ngModule: {name: "virtualRepeatDeferredLoadingDemo", module: "virtualRepeatDeferredLoadingDemo", dependencies: ["ngMaterial"]},
		id: "virtualRepeatdemoDeferredLoading",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/virtualRepeat/demoDeferredLoading/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/virtualRepeat/demoDeferredLoading/script.js"}],
		moduleName: "material.components.virtualRepeat",
		name: "demoDeferredLoading",
		label: "Deferred Loading",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/virtualRepeat/demoDeferredLoading/index.html"}
	}, {
		ngModule: {name: "virtualRepeatHorizontalDemo", module: "virtualRepeatHorizontalDemo", dependencies: ["ngMaterial"]},
		id: "virtualRepeatdemoHorizontalUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/virtualRepeat/demoHorizontalUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/virtualRepeat/demoHorizontalUsage/script.js"}],
		moduleName: "material.components.virtualRepeat",
		name: "demoHorizontalUsage",
		label: "Horizontal Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/virtualRepeat/demoHorizontalUsage/index.html"}
	}, {
		ngModule: {name: "virtualRepeatInfiniteScrollDemo", module: "virtualRepeatInfiniteScrollDemo", dependencies: ["ngMaterial"]},
		id: "virtualRepeatdemoInfiniteScroll",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/virtualRepeat/demoInfiniteScroll/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/virtualRepeat/demoInfiniteScroll/script.js"}],
		moduleName: "material.components.virtualRepeat",
		name: "demoInfiniteScroll",
		label: "Infinite Scroll",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/virtualRepeat/demoInfiniteScroll/index.html"}
	}, {
		ngModule: {name: "virtualRepeatScrollToDemo", module: "virtualRepeatScrollToDemo", dependencies: ["ngMaterial"]},
		id: "virtualRepeatdemoScrollTo",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/virtualRepeat/demoScrollTo/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/virtualRepeat/demoScrollTo/script.js"}],
		moduleName: "material.components.virtualRepeat",
		name: "demoScrollTo",
		label: "Scroll To",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/virtualRepeat/demoScrollTo/index.html"}
	}, {
		ngModule: {name: "virtualRepeatVerticalDemo", module: "virtualRepeatVerticalDemo", dependencies: ["ngMaterial"]},
		id: "virtualRepeatdemoVerticalUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/virtualRepeat/demoVerticalUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/virtualRepeat/demoVerticalUsage/script.js"}],
		moduleName: "material.components.virtualRepeat",
		name: "demoVerticalUsage",
		label: "Vertical Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/virtualRepeat/demoVerticalUsage/index.html"}
	}],
	url: "demo/virtualRepeat"
}, {
	name: "whiteframe",
	moduleName: "material.components.whiteframe",
	label: "Whiteframe",
	demos: [{
		ngModule: {name: "whiteframeBasicUsage", module: "whiteframeBasicUsage", dependencies: ["ngMaterial"]},
		id: "whiteframedemoBasicClassUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/whiteframe/demoBasicClassUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/whiteframe/demoBasicClassUsage/script.js"}],
		moduleName: "material.components.whiteframe",
		name: "demoBasicClassUsage",
		label: "Basic Class Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/whiteframe/demoBasicClassUsage/index.html"}
	}, {
		ngModule: {name: "whiteframeDirectiveUsage", module: "whiteframeDirectiveUsage", dependencies: ["ngMaterial"]},
		id: "whiteframedemoDirectiveAttributeUsage",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/whiteframe/demoDirectiveAttributeUsage/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/whiteframe/demoDirectiveAttributeUsage/script.js"}],
		moduleName: "material.components.whiteframe",
		name: "demoDirectiveAttributeUsage",
		label: "Directive Attribute Usage",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/whiteframe/demoDirectiveAttributeUsage/index.html"}
	}, {
		ngModule: {name: "whiteframeDirectiveUsage", module: "whiteframeDirectiveUsage", dependencies: ["ngMaterial"]},
		id: "whiteframedemoDirectiveInterpolation",
		css: [{name: "style.css", label: "style.css", fileType: "css", outputPath: "demo-partials/whiteframe/demoDirectiveInterpolation/style.css"}],
		html: [],
		js: [{name: "script.js", label: "script.js", fileType: "js", outputPath: "demo-partials/whiteframe/demoDirectiveInterpolation/script.js"}],
		moduleName: "material.components.whiteframe",
		name: "demoDirectiveInterpolation",
		label: "Directive Interpolation",
		index: {name: "index.html", label: "index.html", fileType: "html", outputPath: "demo-partials/whiteframe/demoDirectiveInterpolation/index.html"}
	}],
	url: "demo/whiteframe"
}]), angular.module("docsApp").directive("layoutAlign", function () {
	return angular.noop
}).directive("layout", function () {
	return angular.noop
}).directive("docsDemo", ["$mdUtil", function (e) {
	function t(e, t, a, n, o) {
		function l(e) {
			switch (e) {
				case"index.html":
					return "HTML";
				case"script.js":
					return "JS";
				case"style.css":
					return "CSS";
				case"style.global.css":
					return "CSS";
				default:
					return e
			}
		}

		var i = this;
		i.interpolateCode = angular.isDefined(a.interpolateCode), i.demoId = n(a.demoId || "")(e.$parent), i.demoTitle = n(a.demoTitle || "")(e.$parent), i.demoModule = n(a.demoModule || "")(e.$parent), a.$observe("demoTitle", function (e) {
			i.demoTitle = e || i.demoTitle
		}), a.$observe("demoId", function (e) {
			i.demoId = e || i.demoId
		}), a.$observe("demoModule", function (e) {
			i.demoModule = e || i.demoModule
		}), i.files = {css: [], js: [], html: []}, i.addFile = function (e, t) {
			var a = {name: l(e), contentsPromise: t, fileType: e.split(".").pop()};
			t.then(function (e) {
				a.contents = e
			}), "index.html" === e ? i.files.index = a : "readme.html" === e ? i.demoDescription = a : (i.files[a.fileType] = i.files[a.fileType] || [], i.files[a.fileType].push(a)), i.orderedFiles = [].concat(i.files.index || []).concat(i.files.js || []).concat(i.files.css || []).concat(i.files.html || [])
		}, i.editOnCodepen = function () {
			o.editOnCodepen({title: i.demoTitle, files: i.files, id: i.demoId, module: i.demoModule})
		}
	}

	return {
		restrict: "E",
		scope: !0,
		templateUrl: "partials/docs-demo.tmpl.html",
		transclude: !0,
		controller: ["$scope", "$element", "$attrs", "$interpolate", "codepen", t],
		controllerAs: "demoCtrl",
		bindToController: !0
	}
}]).directive("demoFile", ["$q", "$interpolate", function (e, t) {
	function a(a, n) {
		var o = n.contents, l = a.html(), i = n.name;
		return a.contents().remove(), function (a, n, s, r) {
			r.addFile(t(i)(a), e.when(a.$eval(o) || l)), n.remove()
		}
	}

	return {restrict: "E", require: "^docsDemo", compile: a}
}]).filter("toHtml", ["$sce", function (e) {
	return function (t) {
		return e.trustAsHtml(t)
	}
}]), angular.module("docsApp").directive("demoInclude", ["$q", "$compile", "$timeout", function (e, t, a) {
	function n(n, o, l) {
		function i() {
			d.index.contentsPromise.then(function (a) {
				m = angular.element('<div class="demo-content ' + c + '">');
				var l, i, d = !!c;
				d ? (angular.bootstrap(m[0], [c]), l = m.scope(), i = m.injector().get("$compile"), n.$on("$destroy", function () {
					l.$destroy()
				})) : (l = n.$new(), i = t), e.all([s(), r()])["finally"](function () {
					l.$evalAsync(function () {
						o.append(m), m.html(a), i(m.contents())(l)
					})
				})
			})
		}

		function s() {
			return e.all(d.css.map(function (e) {
				return e.contentsPromise
			})).then(function (e) {
				e = e.join("\n");
				var t = angular.element("<style>" + e + "</style>");
				document.body.appendChild(t[0]), n.$on("$destroy", function () {
					t.remove()
				})
			})
		}

		function r() {
			return e.all(d.html.map(function (e) {
				return e.contentsPromise.then(function (t) {
					var a = m.injector().get("$templateCache");
					a.put(e.name, t), n.$on("$destroy", function () {
						a.remove(e.name)
					})
				})
			}))
		}

		var m, d = n.$eval(l.files) || {}, c = n.$eval(l.module) || "";
		a(i)
	}

	return {restrict: "E", link: n}
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/contributors.tmpl.html", '<div ng-controller="GuideCtrl" class="doc-content">\n  <md-content>\n    <p>\n      We are thankful for the amazing community and <em>contributors</em> to AngularJS Material.<br/>\n      Shown below is a list of all our contributors: developers who submitted fixes and improvements to AngularJS Material.\n    </p>\n    <md-divider></md-divider>\n\n    <h2>Contributors</h2>\n\n    <p style="margin-top:-10px;"> (sorted by GitHub name)</p>\n    <br/>\n\n    <div class="contributor_tables">\n\n      <!-- User the \'contributors.json\' generated by \'npm run contributors\' -->\n\n      <table ng-repeat="row in github.contributors"> \n        <thead>\n        <tr>\n          <th style="text-align:center" ng-repeat="user in row">\n            <a href="{{user.html_url}}" >\n              <img  alt="{{user.login}}"\n                    ng-src="{{user.avatar_url}}"\n                    width="{{github.imageSize}}" class="md-avatar">\n            </a>\n          </th>\n        </tr>\n        </thead>\n        <tbody>\n        <tr>\n          <td style="text-align:center" ng-repeat="user in row">\n            <a href="{{user.html_url}}" class="md-primary">{{user.login}}</a>\n          </td>\n          <td></td>\n        </tr>\n        </tbody>\n      </table>\n\n    </div>\n  </md-content>\n</div>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/demo.tmpl.html", '<docs-demo\n    ng-repeat="demo in demos"\n    demo-id="{{demo.id}}"\n    demo-title="{{demo.label}}"\n    demo-module="{{demo.ngModule.module}}">\n  <demo-file\n      ng-repeat="file in demo.$files"\n      name="{{file.name}}"\n      contents="file.httpPromise"></demo-file>\n</docs-demo>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/docs-demo.tmpl.html", '<div class="doc-demo-content doc-content">\n  <div flex layout="column" style="z-index:1">\n\n    <div class="doc-description" ng-bind-html="demoCtrl.demoDescription.contents | toHtml"></div>\n\n    <div ng-transclude></div>\n\n    <section class="demo-container md-whiteframe-z1"\n      ng-class="{\'show-source\': demoCtrl.$showSource}" >\n\n      <md-toolbar class="demo-toolbar md-primary">\n        <div class="md-toolbar-tools">\n          <h3>{{demoCtrl.demoTitle}}</h3>\n          <span flex></span>\n          <md-button\n            class="md-icon-button"\n            aria-label="View Source"\n            ng-class="{ active: demoCtrl.$showSource }"\n            ng-click="demoCtrl.$showSource = !demoCtrl.$showSource">\n              <md-tooltip md-autohide>View Source</md-tooltip>\n              <md-icon md-svg-src="img/icons/ic_code_24px.svg"></md-icon>\n          </md-button>\n          <md-button\n              ng-hide="{{exampleNotEditable}}"\n              hide-sm\n              ng-click="demoCtrl.editOnCodepen()"\n              aria-label="Edit on CodePen"\n              class="md-icon-button">\n            <md-tooltip md-autohide>Edit on CodePen</md-tooltip>\n            <md-icon md-svg-src="img/icons/codepen-logo.svg"></md-icon>\n          </md-button>\n        </div>\n      </md-toolbar>\n\n      <!-- Source views -->\n      <md-tabs class="demo-source-tabs md-primary" ng-show="demoCtrl.$showSource" style="min-height: 0;">\n        <md-tab ng-repeat="file in demoCtrl.orderedFiles" label="{{file.name}}">\n          <md-content md-scroll-y class="demo-source-container">\n            <hljs class="no-header" code="file.contentsPromise" lang="{{file.fileType}}" should-interpolate="demoCtrl.interpolateCode">\n            </hljs>\n          </md-content>\n        </md-tab>\n      </md-tabs>\n\n      <!-- Live Demos -->\n      <demo-include files="demoCtrl.files" module="demoCtrl.demoModule" class="{{demoCtrl.demoId}}">\n      </demo-include>\n    </section>\n\n  </div>\n</div>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/getting-started.tmpl.html", '<div ng-controller="GuideCtrl" class="doc-content">\n  <md-content>\n    <p><em>New to AngularJS? Before getting into AngularJS Material, it might be helpful to:</em></p>\n\n    <ul>\n      <li>\n        watch the videos about <a\n          href="https://egghead.io/articles/new-to-angularjs-start-learning-here" target="_blank"\n          title="AngularJS Framework">AngularJS framework</a></li>\n      <li>\n        read the\n        <a href="https://material.google.com/" target="_blank"\n           title="Material Design">Material Design </a> specifications for components,\n        animations, styles, and layouts.\n      </li>\n    </ul>\n\n    <h2>How do I start with AngularJS Material?</h2>\n    <ul style="margin-bottom: 2em;">\n      <li>\n        Visit the <a href="http://codepen.io/team/AngularMaterial/" target="_blank"\n                       title="Codepen Material Community">CodePen Community for AngularJS Material</a>\n      </li>\n\n      <li>\n        <a href="https://github.com/angular/material-start/tree/es6-tutorial" target="_blank"\n             title="Material-Start Tutorial">Learn with Material-Start: 10-step Tutorial (es6)</a>\n      </li>\n\n      <li>\n        <a href="https://github.com/angular/material-start/tree/es6" target="_blank"\n             title="Material Start - ES6">Learn with Material-Start: Completed (es6)</a>\n      </li>\n\n      <li>\n        <a href="https://github.com/angular/material-start/tree/typescript" target="_blank"\n           title="Material Start - Typescript">Learn with Material-Start: Completed (Typescript)</a>\n      </li>\n\n      <li>\n        <a href="https://github.com/angular/material-start/tree/master" target="_blank"\n           title="Material-Start - ES5">Learn with Material-Start: Completed (es5)</a>\n      </li>\n\n      <li>\n        <a href="http://codepen.io/team/AngularMaterial/pen/RrbXyW" target="_blank">Start with a\n        blank CodePen Material Application</a>\n      </li>\n\n      <li style="margin-bottom: 30px;">\n        <a href="https://github.com/angular/material-start" target="_blank"\n           title="GitHub Starter Project">Use the Github Starter Project</a>\n      </li>\n\n      <li>Use the "Edit on CodePen" button on any of our Demos<br/>\n        <img\n            src="https://cloud.githubusercontent.com/assets/210413/11568997/ed86795a-99b4-11e5-898e-1da172be80da.png"\n            style="width:75%;margin: 10px 30px 0 0">\n      </li>\n\n    </ul>\n\n    <h3>Build a Material Application (blank shell)</h3>\n\n    <p>\n      See this example application on CodePen that loads\n      the AngularJS Material library from the Google CDN:\n    </p>\n\n    <iframe height=\'777\' scrolling=\'no\'\n            src=\'//codepen.io/team/AngularMaterial/embed/RrbXyW/?height=777&theme-id=21180&default-tab=html\'\n            frameborder=\'no\' allowtransparency=\'true\' allowfullscreen=\'true\' style=\'width: 100%;\'>\n      See the Pen <a\n        href=\'http://codepen.io/team/AngularMaterial/pen/RrbXyW/\'>AngularJS Material - Blank\n      Starter</a> by AngularJS\n      Material (<a href=\'http://codepen.io/AngularMaterial\'>@AngularMaterial</a>) on <a\n        href=\'http://codepen.io\'>CodePen</a>.\n    </iframe>\n\n\n    <p>\n      Now just your add your AngularJS Material components and other HTML content to your Blank\n      starter app.\n    </p>\n\n    <br/>\n\n    <hr>\n\n    <h3>Our CodePen Community</h3>\n    <p>\n      You can also visit our\n      <a href="http://codepen.io/team/AngularMaterial/" target="_blank"\n         title="Codepen Community">CodePen Community</a> to explore more samples and ideas.\n    </p>\n    <div layout-align="center" style="width: 100%">\n      <a href="http://codepen.io/collection/XExqGz/" target="_blank" title="Codepen Community"\n         style="text-decoration:none; border: 0 none;">\n        <img\n            src="https://cloud.githubusercontent.com/assets/210413/11613879/544f0b1e-9bf6-11e5-9923-27dd0d891bfd.png"\n            style="text-decoration:none; border: 0 none;width: 100%">\n      </a>\n    </div>\n\n\n    <br/><br/>\n    <hr>\n\n    <h3>Installing the AngularJS Material Libraries</h3>\n\n    <p>\n      You can install the AngularJS Material library (and its dependent libraries) in your local\n      project using either\n      <a href="https://github.com/angular/bower-material/#installing-angular-material"\n         target="_blank">NPM, JSPM, or Bower</a>.\n    </p>\n\n    <p>\n      AngularJS Material also integrates with some additional, optional libraries which you may elect\n      to include:\n    </p>\n\n    <ul style="margin-bottom: 2em;">\n      <li>\n        <a href="https://docs.angularjs.org/api/ngMessages">ngMessages</a>\n        - Provides a consistent mechanism for displaying form errors and other messages.\n      </li>\n      <li>\n        <a href="https://docs.angularjs.org/api/ngSanitize">ngSanitize</a>\n        - The ngSanitize module provides functionality to sanitize HTML content in Material\n        components.\n      </li>\n\n      <li>\n        <a href="https://docs.angularjs.org/api/ngRoute">ngRoute</a>\n        - Provides a clean routing system for your application.\n      </li>\n    </ul>\n\n    <br/>\n    <hr>\n\n    <h3>Unsupported Integrations</h3>\n    <p>\n      AngularJS Material v1.0 has known integration issues with the following libraries:\n    </p>\n    <ul style="margin-bottom: 2em;">\n      <li>\n        <a href="https://docs.angularjs.org/api/ngTouch">ngTouch</a>\n        - AngularJS Material conflicts with ngTouch for click, tap, and swipe support on touch-enabled\n        devices.\n      </li>\n\n      <li>\n        <a href="http://ionicframework.com/">Ionic</a>\n        - Open-source SDK for developing hybrid mobile apps with Web technologies has touch support\n        that interferes with AngularJS Material\'s mobile gesture features.\n      </li>\n    </ul>\n\n    <br/>\n    <h2>Contributing to AngularJS Material</h2>\n    <ul style="margin-bottom: 2em;">\n      <li>\n        Please read our <a href="https://github.com/angular/material#contributing">contributor\n        guidelines</a>.\n      </li>\n\n      <li>\n        To contribute, fork our GitHub <a href="https://github.com/angular/material">repository</a>.\n      </li>\n\n      <li>\n        For problems,\n        <a href="https://github.com/angular/material/issues?q=is%3Aissue+is%3Aopen"\n           target="_blank">search the GitHub Issues</a> and/or\n        <a href="https://github.com/angular/material/issues/new"\n           target="_blank">create a New Issue</a>.\n      </li>\n\n      <li>For questions,\n        <a href="https://groups.google.com/forum/#!forum/ngmaterial"\n           target="_blank">search the Forum</a> and/or post a new question.\n      </li>\n\n      <li>\n        Join the <a href="https://gitter.im/angular/material" target="_blank">Gitter Chat</a>.\n      </li>\n    </ul>\n  </md-content>\n</div>\n');
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/home.tmpl.html", '<div ng-controller="HomeCtrl" class="doc-content">\n  <md-content>\n    <h2 class="md-headline" style="margin-top: 0;">What is AngularJS Material?</h2>\n    <p>\n      AngularJS Material is both a UI Component framework and a reference implementation of Google\'s\n      Material Design Specification. This project provides a set of reusable, well-tested, and\n      accessible UI components based on Material Design.\n    </p>\n    <ul class="buckets" layout layout-align="center center" layout-wrap>\n      <li flex="50" flex-gt-md="25" ng-repeat="(index, link) in [\n        { href: \'./getting-started\', icon: \'school\', text: \'Getting Started\' },\n        { href: \'./contributors\', icon: \'school\', text: \'Contributors\' },\n        { href: \'./demo\', icon: \'play_circle_fill\', text: \'Demos\' },\n        { href: \'./CSS/typography\', icon: \'build\', text: \'Customization\' },\n        { href: \'./api\', icon: \'code\', text: \'API Reference\' }\n      ]">\n        <md-button\n            class="md-primary md-raised"\n            ng-href="{{link.href}}"\n            aria-label="{{link.text}}">\n          <md-icon class="block" md-svg-src="img/icons/ic_{{link.icon}}_24px.svg"></md-icon>\n          {{link.text}}\n        </md-button>\n      </li>\n    </ul>\n\n    <br/>\n    <h2 class="md-headline">What about Angular?</h2>\n    <p>\n      AngularJS Material recently released Version 1 which we consider to be stable and ready for\n      production use. Developers should note that AngularJS Material works only with AngularJS 1.x.\n    </p>\n    <ul>\n      <li>Current AngularJS Material development efforts are focused on bug fixes and minor improvements.</li>\n      <li>AngularJS Material development is also in-progress at the <a href="https://github.com/angular/material2">angular/material2</a> GitHub repository.</li>\n    </ul>\n    <p>\n      Please refer to our changelog for up-to-date listings of all v1.x improvements and breaking changes.\n    </p>\n     <ul class="buckets" layout layout-align="center center" layout-wrap>\n      <li flex="100" flex-gt-xs="50" ng-repeat="(index, link) in [\n        {\n          href: \'https://github.com/angular/material/blob/master/CHANGELOG.md\',\n          icon: \'access_time\',\n          text: \'Changelog\'\n        }\n      ]">\n        <md-button\n            class="md-primary md-raised"\n            ng-href="{{link.href}}"\n            aria-label="{{link.text}}">\n          <md-icon class="block" md-svg-src="img/icons/ic_{{link.icon}}_24px.svg"></md-icon>\n          {{link.text}}<br/>\n          <div style="text-transform: none;margin-top:-15px;font-size:1.0em;">AngularJS Material v1.x </div>\n        </md-button>\n      </li>\n    </ul>\n\n    <md-divider></md-divider>\n\n    <br/>\n    <h2 class="md-headline">Training Videos:</h2>\n    <p>\n      Here are some video courses that will help jump start your development with AngularJS Material.\n    </p>\n    <ul class="buckets" layout layout-align="center center" layout-wrap>\n      <li flex="100" flex-gt-xs="50" ng-repeat="(index, link) in [\n        { href: \'https://egghead.io/series/angular-material-introduction\', icon: \'ondemand_video\', text: \'Introduction to AngularJS Material\', site : \'EggHead\', access : \'free\'},\n        { href: \'https://app.pluralsight.com/player?author=ajden-towfeek&name=angular-material-fundamentals-m0&mode=live&clip=0&course=angular-material-fundamentals\', icon: \'ondemand_video\', text: \'AngularJS Material Fundamentals\', site : \'Pluralsight\', access: \'member\'},\n      ]">\n        <md-button\n            class="md-primary md-raised"\n            target="_blank"\n            aria-label="{{link.text}}"\n            ng-href="{{link.href}}">\n          <md-icon class="block" md-svg-src="img/icons/ic_{{link.icon}}_24px.svg"></md-icon>\n          {{link.site}} | <span style="color: rgb(255,82,82); text-transform: none;">{{link.text}}</span> | <span class="training_info">{{link.access}}</span>\n        </md-button>\n      </li>\n    </ul>\n\n\n    <br/>\n    <h2 class="md-headline">Conference Presentations:</h2>\n    <p>\n      Here are some conference presentations that will provide overviews for your development with AngularJS Material.\n    </p>\n    <ul class="buckets" layout layout-align="center center" layout-wrap>\n      <li flex="100" flex-gt-xs="50" ng-repeat="(index, link) in [\n        { href: \'https://www.youtube.com/watch?v=Qi31oO5u33U\', icon: \'ondemand_video\', text: \'Building with AngularJS Material\', site : \'ng-conf\',  date: \'2015\'},\n        { href: \'https://www.youtube.com/watch?v=363o4i0rdvU\', icon: \'ondemand_video\', text: \'AngularJS Material in Practice\', site : \'AngularConnect\', date:\'2015\'},\n      ]">\n        <md-button\n            class="md-primary md-raised"\n            target="_blank"\n            aria-label="{{link.text}}"\n            ng-href="{{link.href}}">\n          <md-icon class="block" md-svg-src="img/icons/ic_{{link.icon}}_24px.svg"></md-icon>\n          <span class="training_site">{{link.site}}</span> | <span class="training_link">{{link.text}}</span> | <span class="training_info">{{link.date}}</span>\n        </md-button>\n      </li>\n    </ul>\n\n\n    <br/>\n    <h2 class="md-headline">Google\'s Material Design</h2>\n    <p>\n      Material Design is a specification for a unified system of visual, motion, and interaction\n      design that adapts across different devices and different screen sizes.\n    </p>\n    <ul class="buckets" layout layout-align="center center" layout-wrap>\n      <li flex="100" flex-gt-xs="50" ng-repeat="(index, link) in [\n        { href: \'https://www.youtube.com/watch?v=Q8TXgCzxEnw\', icon: \'ondemand_video\', text: \'Watch a video\', site : \'Google\' },\n        { href: \'http://www.google.com/design/spec/material-design/\', icon: \'launch\', text: \'Learn More\', site : \'Google\' }\n      ]">\n        <md-button\n            class="md-primary md-raised"\n            target="_blank"\n            aria-label="{{link.text}}"\n            ng-href="{{link.href}}">\n          <md-icon class="block" md-svg-src="img/icons/ic_{{link.icon}}_24px.svg"></md-icon>\n          {{link.site}} | <span class="training_link"> {{link.text}} </span>\n        </md-button>\n      </li>\n    </ul>\n\n    <br/>\n    <p class="md-caption" style="text-align: center; margin-bottom: 0;">\n      These docs were generated from\n      (<a ng-href="{{BUILDCONFIG.repository}}/{{menu.version.current.github}}" target="_blank" class="md-accent" >\n      v{{BUILDCONFIG.version}} - SHA {{BUILDCONFIG.commit.substring(0,7)}}</a>)\n      on (<strong>{{BUILDCONFIG.date}}</strong>) GMT.\n    </p>\n  </md-content>\n</div>\n\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/layout-alignment.tmpl.html", '<div ng-controller="LayoutCtrl" class="layout-content" ng-cloak>\n\n  <p>\n    The <code>layout-align</code> directive takes two words.\n    The first word says how the children will be aligned in the layout\'s direction, and the second word says how the children will be aligned perpendicular to the layout\'s direction.</p>\n\n    <p>Only one value is required for this directive.\n    For example, <code>layout="row" layout-align="center"</code> would make the elements\n    center horizontally and use the default behavior vertically.</p>\n\n    <p><code>layout="column" layout-align="center end"</code> would make\n    children align along the center vertically and along the end (right) horizontally. </p>\n\n\n  <table class="md-api-table">\n         <thead>\n           <tr>\n             <th>API</th>\n             <th>Sets child alignments within the layout container</th>\n           </tr>\n         </thead>\n          <tr>\n            <td>layout-align</td>\n            <td>Sets default alignment unless overridden by another breakpoint.</td>\n          </tr>\n          <tr>\n           <td>layout-align-xs</td>\n           <td>width &lt; <b>600</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-gt-xs</td>\n           <td>width &gt;= <b>600</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-sm</td>\n           <td><b>600</b>px &lt;= width &lt; <b>960</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-gt-sm</td>\n           <td>width &gt;= <b>960</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-md</td>\n           <td><b>960</b>px &lt;= width &lt; <b>1280</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-gt-md</td>\n           <td>width &gt;= <b>1280</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-lg</td>\n           <td><b>1280</b>px &lt;= width &lt; <b>1920</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-gt-lg</td>\n           <td>width &gt;= <b>1920</b>px</td>\n         </tr>\n         <tr>\n           <td>layout-align-xl</td>\n           <td>width &gt;= <b>1920</b>px</td>\n         </tr>\n        </table>\n\n  <br/>\n\n  <p>\n   Below is an interactive demo that lets you explore the visual results of the different settings:\n  </p>\n\n  <div>\n    <docs-demo demo-title=\'layout="{{layoutDemo.direction}}" &nbsp; &nbsp; &nbsp; layout-align="{{layoutAlign()}}"\'\n               class="small-demo colorNested" interpolate-code="true">\n      <demo-file name="index.html">\n        <div layout="{{layoutDemo.direction}}" layout-align="{{layoutAlign()}}">\n          <div>one</div>\n          <div>two</div>\n          <div>three</div>\n        </div>\n      </demo-file>\n    </docs-demo>\n  </div>\n\n  <div layout="column" layout-gt-sm="row" layout-align="space-around">\n\n    <div>\n      <md-subheader>Layout Direction</md-subheader>\n      <md-radio-group ng-model="layoutDemo.direction">\n        <md-radio-button value="row">row</md-radio-button>\n        <md-radio-button value="column">column</md-radio-button>\n      </md-radio-group>\n    </div>\n    <div>\n      <md-subheader>Alignment in Layout Direction ({{layoutDemo.direction == \'row\' ? \'horizontal\' : \'vertical\'}})</md-subheader>\n      <md-radio-group ng-model="layoutDemo.mainAxis">\n        <md-radio-button value="">none</md-radio-button>\n        <md-radio-button value="start">start (default)</md-radio-button>\n        <md-radio-button value="center">center</md-radio-button>\n        <md-radio-button value="end">end</md-radio-button>\n        <md-radio-button value="space-around">space-around</md-radio-button>\n        <md-radio-button value="space-between">space-between</md-radio-button>\n      </md-radio-group>\n    </div>\n    <div>\n      <md-subheader>Alignment in Perpendicular Direction ({{layoutDemo.direction == \'column\' ? \'horizontal\' : \'vertical\'}})</md-subheader>\n      <md-radio-group ng-model="layoutDemo.crossAxis">\n        <md-radio-button value="none"><em>none</em></md-radio-button>\n        <md-radio-button value="start">start</md-radio-button>\n        <md-radio-button value="center">center</md-radio-button>\n        <md-radio-button value="end">end</md-radio-button>\n        <md-radio-button value="stretch">stretch (default)</md-radio-button>\n      </md-radio-group>\n    </div>\n\n  </div>\n</div>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/layout-children.tmpl.html", '<div ng-controller="LayoutCtrl" class="layout-content" ng-cloak>\n\n  <h3>Children within a Layout Container</h3>\n\n  <p>\n    To customize the size and position of elements in a layout <b>container</b>, use the\n    <code>flex</code>, <code>flex-order</code>, and <code>flex-offset</code> attributes on the container\'s <u>child</u> elements:\n  </p>\n\n  <docs-demo demo-title="Flex Directive" class="small-demo colorNested">\n    <demo-file name="index.html">\n      <div layout="row">\n        <div flex="20">\n          [flex="20"]\n        </div>\n        <div flex="70">\n          [flex="70"]\n        </div>\n        <div flex hide-sm hide-xs>\n          [flex]\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <p>\n    Add the <code>flex</code> directive to a layout\'s child element and the element will flex (grow or shrink) to fit\n    the area unused by other elements. <code>flex</code> defines how the element will adjust its size with respect to its\n    <u>parent</u> container and the other elements within the container.\n  </p>\n\n  <docs-demo demo-title="Flex Percent Values" class="small-demo colorNested-noPad">\n    <demo-file name="index.html">\n      <div layout="row" layout-wrap>\n        <div flex="30">\n          [flex="30"]\n        </div>\n        <div flex="45">\n          [flex="45"]\n        </div>\n        <div flex="25">\n          [flex="25"]\n        </div>\n        <div flex="33">\n          [flex="33"]\n        </div>\n        <div flex="66">\n          [flex="66"]\n        </div>\n        <div flex="50">\n          [flex="50"]\n        </div>\n        <div flex>\n          [flex]\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n\n  <p>\n    A layout child\'s <code>flex</code> directive can be given an integer value from 0-100.\n    The element will stretch to the percentage of available space matching the value. Currently, the <code>flex</code>\n    directive value is restricted to multiples of five, 33 or 66.\n  </p>\n\n  <p> For example: <code>flex="5", flex="20", flex="33", flex="50", flex="66", flex="75", ... flex="100"</code>.</p>\n\n  <docs-demo demo-title="Responsive Flex Directives" class="small-demo colorNested-noPad">\n    <demo-file name="index.html">\n      <div layout="row">\n        <div flex-gt-sm="66" flex="33">\n          flex 33% on mobile, <br/>and 66% on gt-sm devices.\n        </div>\n        <div flex-gt-sm="33" flex="66">\n          flex 66%  on mobile, <br/>and 33% on gt-sm devices.\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n\n  <p>\n    See the <a href="layout/options">layout options page</a> for more information on responsive flex directives like\n    <code>hide-sm</code> and <code>layout-wrap</code> used in the above examples.\n  </p>\n\n  <br/>\n  <hr>\n  <br/>\n\n  <h3>Additional Flex Values</h3>\n\n  <p>\n    There are additional flex values provided by AngularJS Material to improve flexibility and to make the API\n    easier to understand.\n  </p>\n\n  <docs-demo demo-title="Other Flex Values" class="small-demo colorNested-noPad">\n    <demo-file name="index.html">\n      <div layout="row" layout-wrap>\n        <div flex="none">\n          [flex="none"]\n        </div>\n        <div flex>\n          [flex]\n        </div>\n        <div flex="nogrow">\n          [flex="nogrow"]\n        </div>\n        <div flex="grow">\n          [flex="grow"]\n        </div>\n        <div flex="initial">\n          [flex="initial"]\n        </div>\n        <div flex="auto">\n          [flex="auto"]\n        </div>\n        <div flex="noshrink">\n          [flex="noshrink"]\n        </div>\n        <div flex="0">\n          [flex="0"]\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n\n  <table class="md-api-table">\n    <tr>\n      <td>flex</td>\n      <td>\n        Will grow and shrink as needed. Starts with a size of 0%. Same as <code>flex="0"</code>.\n        <br />\n        <br />\n        <b>Note:</b> There is a known bug with this attribute in IE11 when the parent container has\n        no explicit height set. See our\n        <a ng-href="layout/tips#layout-column-0px-ie11">Troubleshooting</a> page for more info.\n      </td>\n    </tr>\n    <tr>\n      <td>flex="none"</td>\n      <td>Will not grow or shrink. Sized based on its <code>width</code> and <code>height</code> values.</td>\n    </tr>\n    <tr>\n      <td>flex="initial"</td>\n      <td>Will shrink as needed. Starts with a size based on its <code>width</code> and <code>height</code> values.</td>\n    </tr>\n    <tr>\n      <td>flex="auto"</td>\n      <td>Will grow and shrink as needed. Starts with a size based on its <code>width</code> and <code>height</code> values.</td>\n    </tr>\n    <tr>\n      <td>flex="grow"</td>\n      <td>Will grow and shrink as needed. Starts with a size of 100%. Same as <code>flex="100"</code>.</td>\n    </tr>\n    <tr>\n      <td>flex="nogrow"</td>\n      <td>Will shrink as needed, but won\'t grow. Starts with a size based on its <code>width</code> and <code>height</code> values.</td>\n    </tr>\n    <tr>\n      <td>flex="noshrink"</td>\n      <td>Will grow as needed, but won\'t shrink. Starts with a size based on its <code>width</code> and <code>height</code> values.</td>\n    </tr>\n  </table>\n\n\n  <br/>\n  <hr>\n  <br/>\n\n  <h3>Ordering HTML Elements</h3>\n\n  <p>\n    Add the <code>flex-order</code> directive to a layout child to set its\n    order position within the layout container. Any integer value from -20 to 20 is accepted.\n  </p>\n\n  <docs-demo demo-title="Flex-Order Directive" class="small-demo colorNested">\n    <demo-file name="index.html">\n      <div layout="row">\n        <div flex flex-order="-1">\n          <p>[flex-order="-1"]</p>\n        </div>\n        <div flex flex-order="1" flex-order-gt-md="3">\n          <p hide-gt-md>[flex-order="1"]</p>\n          <p hide show-gt-md>[flex-order-gt-md="3"]</p>\n        </div>\n        <div flex flex-order="2">\n          <p>[flex-order="2"]</p>\n        </div>\n        <div flex flex-order="3" flex-order-gt-md="1">\n          <p hide-gt-md>[flex-order="3"]</p>\n          <p hide show-gt-md>[flex-order-gt-md="1"]</p>\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <table class="md-api-table">\n      <thead>\n        <tr>\n          <th>API</th>\n          <th>Device <b>width</b> when breakpoint overrides default</th>\n        </tr>\n      </thead>\n       <tr>\n         <td>flex-order</td>\n         <td>Sets default layout order unless overridden by another breakpoint.</td>\n       </tr>\n    <tr>\n        <td>flex-order-xs</td>\n           <td>width &lt; <b>600</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-gt-xs</td>\n           <td>width &gt;= <b>600</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-sm</td>\n           <td><b>600</b>px &lt;= width &lt; <b>960</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-gt-sm</td>\n           <td>width &gt;= <b>960</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-md</td>\n           <td><b>960</b>px &lt;= width &lt; <b>1280</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-gt-md</td>\n           <td>width &gt;= <b>1280</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-lg</td>\n           <td><b>1280</b>px &lt;= width &lt; <b>1920</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-gt-lg</td>\n           <td>width &gt;= <b>1920</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-order-xl</td>\n           <td>width &gt;= <b>1920</b>px</td>\n         </tr>\n     </table>\n\n\n  <p>\n    See the <a href="layout/options">layout options page</a> for more information on directives like\n    <code>hide</code>, <code>hide-gt-md</code>, and <code>show-gt-md</code> used in the above examples.\n  </p>\n\n  <br/>\n  <hr>\n  <br/>\n\n  <h3>Add Offsets to the Preceding HTML Elements</h3>\n\n  <p>\n    Add the <code>flex-offset</code> directive to a layout child to set its\n    offset percentage within the layout container. Values must be multiples\n    of <code>5</code> or <code>33</code> / <code>66</code>. These offsets establish a <code>margin-left</code>\n    with respect to the preceding element or the containers left boundary.\n  </p>\n\n  <p>\n      When using <code>flex-offset</code> the margin-left offset is applied... regardless of your choice of <code>flex-order</code>.\n      or if you use a <code>flex-direction: reverse</code>.\n    </p>\n\n  <docs-demo demo-title="Flex-Offset Directive" class="small-demo colorNested">\n    <demo-file name="index.html">\n      <div layout="row">\n        <div flex="66" flex-offset="15">\n          [flex-offset="15"]\n          [flex="66"]\n        </div>\n        <div flex>\n          [flex]\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <table class="md-api-table">\n        <thead>\n          <tr>\n            <th>API</th>\n            <th>Device width when breakpoint overrides default</th>\n          </tr>\n        </thead>\n         <tr>\n           <td>flex-offset</td>\n           <td>Sets default margin-left offset (<b>%-based</b>) unless overridden by another breakpoint.</td>\n         </tr>\n    <tr>\n           <td>flex-offset-xs</td>\n           <td>width &lt; <b>600</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-gt-xs</td>\n           <td>width &gt;= <b>600</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-sm</td>\n           <td><b>600</b>px &lt;= width &lt; <b>960</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-gt-sm</td>\n           <td>width &gt;= <b>960</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-md</td>\n           <td><b>960</b>px &lt;= width &lt; <b>1280</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-gt-md</td>\n           <td>width &gt;= <b>1280</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-lg</td>\n           <td><b>1280</b>px &lt;= width &lt; <b>1920</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-gt-lg</td>\n           <td>width &gt;= <b>1920</b>px</td>\n         </tr>\n         <tr>\n           <td>flex-offset-xl</td>\n           <td>width &gt;= <b>1920</b>px</td>\n         </tr>\n       </table>\n\n\n</div>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/layout-container.tmpl.html", '<div ng-controller="LayoutCtrl" class="layout-content" ng-cloak>\n\n  <h3>Layout and Containers</h3>\n\n  <p>\n    Use the <code>layout</code> directive on a container element to specify the layout direction for its children:\n    horizontally in a row (<code>layout="row"</code>) or vertically in a column (<code>layout="column"</code>).\n    Note that <code>row</code> is the default layout direction if you specify the <code>layout</code> directive without a value.\n  </p>\n\n  <table>\n    <tr>\n      <td style="font-weight: bold; background-color: #DBEEF5">row</td>\n      <td style="padding-left: 10px;">Items arranged horizontally. <code>max-height = 100%</code> and <code>max-width</code>  is the width of the items in the container.</td>\n    </tr>\n    <tr>\n      <td style="font-weight: bold; background-color: #DBEEF5 ">column</td>\n      <td style="padding-left: 10px;">Items arranged vertically. <code>max-width = 100%</code>  and <code>max-height</code> is the height of the items in the container.</td>\n    </tr>\n  </table>\n\n  <br/>\n\n  <docs-demo demo-title="Layout Directive" class="small-demo colorNested">\n    <demo-file name="index.html">\n    <div layout="row">\n      <div flex>First item in row</div>\n      <div flex>Second item in row</div>\n    </div>\n    <div layout="column">\n      <div flex>First item in column</div>\n      <div flex>Second item in column</div>\n    </div>\n    </demo-file>\n  </docs-demo>\n\n  <p>\n      Note that <code>layout</code> only affects the flow direction for that container\'s <b>immediate</b> children.\n    </p>\n\n  <hr>\n\n  <br/>\n  <h3>Layouts and Responsive Breakpoints</h3>\n\n  <p>\n    As discussed in the <a href="layout/introduction">Layout Introduction page</a> you can\n    make your layout change depending upon the device view size by using <b>breakpoint alias</b> suffixes.\n   </p>\n\n  <p>\n    To make your layout automatically change depending upon the device screen size, use one to the following <code>layout</code>\n    APIs to set the layout direction for devices with view widths:\n  </p>\n\n   <table class="md-api-table">\n    <thead>\n      <tr>\n        <th>API</th>\n        <th>Device width when breakpoint overrides default</th>\n      </tr>\n    </thead>\n     <tr>\n       <td>layout</td>\n       <td>Sets default layout direction unless overridden by another breakpoint.</td>\n     </tr>\n     <tr>\n       <td>layout-xs</td>\n       <td>width &lt; <b>600</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-gt-xs</td>\n       <td>width &gt;= <b>600</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-sm</td>\n       <td><b>600</b>px &lt;= width &lt; <b>960</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-gt-sm</td>\n       <td>width &gt;= <b>960</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-md</td>\n       <td><b>960</b>px &lt;= width &lt; <b>1280</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-gt-md</td>\n       <td>width &gt;= <b>1280</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-lg</td>\n       <td><b>1280</b>px &lt;= width &lt; <b>1920</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-gt-lg</td>\n       <td>width &gt;= <b>1920</b>px</td>\n     </tr>\n     <tr>\n       <td>layout-xl</td>\n       <td>width &gt;= <b>1920</b>px</td>\n     </tr>\n   </table>\n   <br/>\n\n  <p><a\n      href="https://camo.githubusercontent.com/ad81ae92f8b4285747ce4e48007bf3f104dd5630/687474703a2f2f6d6174657269616c2d64657369676e2e73746f726167652e676f6f676c65617069732e636f6d2f7075626c6973682f6d6174657269616c5f765f342f6d6174657269616c5f6578745f7075626c6973682f3042386f6c5631354a3761625053474678656d46695156527462316b2f6c61796f75745f61646170746976655f627265616b706f696e74735f30312e706e67"\n      target="_blank" style="text-decoration: none;border: 0 none;">\n      <img\n      src="https://camo.githubusercontent.com/ad81ae92f8b4285747ce4e48007bf3f104dd5630/687474703a2f2f6d6174657269616c2d64657369676e2e73746f726167652e676f6f676c65617069732e636f6d2f7075626c6973682f6d6174657269616c5f765f342f6d6174657269616c5f6578745f7075626c6973682f3042386f6c5631354a3761625053474678656d46695156527462316b2f6c61796f75745f61646170746976655f627265616b706f696e74735f30312e706e67"\n      alt=""\n      style="max-width:100%;text-decoration: none;border: 0 none;"></a>\n  </p>\n\n\n  <p>\n    For the demo below, as you shrink your browser window width notice the flow direction changes to <code>column</code>\n    for mobile devices (<code>xs</code>). And as you expand it will reset to <code>row</code>\n    for <code>gt-sm</code> view sizes.\n\n  </p>\n\n  <docs-demo demo-title="Responsive Layouts" class="small-demo colorNested">\n    <demo-file name="index.html">\n      <div layout="row" layout-xs="column">\n        <div flex>\n          I\'m above on mobile, and to the left on larger devices.\n        </div>\n        <div flex>\n          I\'m below on mobile, and to the right on larger devices.\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n\n\n  <p>\n    See the <a href="layout/options">Layout Options page</a> for more options such as padding, alignments, etc.\n  </p>\n\n\n\n </div>\n\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/layout-introduction.tmpl.html", '<div ng-controller="LayoutCtrl" class="layout-content" ng-cloak>\n\n  <h3>Overview</h3>\n  <p>\n    AngularJS Material\'s Layout features provide sugar to enable developers to more easily create modern,\n    responsive layouts on top of CSS3 <a href="http://www.w3.org/TR/css3-flexbox/">flexbox</a>.\n    The layout API consists of a set of AngularJS directives that can\n    be applied to any of your application\'s HTML content.\n  </p>\n\n\n  <p>\n    Using <b> HTML Directives</b> as the API provides an easy way to set a value (eg. <code>layout="row"</code>) and\n    helps with separation of concerns: Attributes define layout while CSS classes assign styling.\n  </p>\n\n\n  <table class="md-api-table">\n    <thead>\n    <tr>\n      <th>HTML Markup API</th>\n      <th>Allowed values (raw or interpolated)</th>\n    </tr>\n    </thead>\n    <tbody>\n    <tr>\n      <td>layout</td>\n      <td><code>row | column</code></td>\n    </tr>\n    <tr>\n      <td>flex</td>\n      <td> integer (increments of 5 for 0%->100%, 100%/3, 200%/3)</td>\n    </tr>\n    <tr>\n      <td>flex-order</td>\n      <td>integer values from -20 to 20</td>\n    </tr>\n    <tr>\n      <td>flex-offset</td>\n      <td>integer (increments of 5 for 0%->95%, 100%/3, 200%/3)</td>\n    </tr>\n    <tr>\n      <td>layout-align</td>\n      <td><code>start|center|end|space-around|space-between</code> <code>start|center|end|stretch</code></td>\n    </tr>\n    <tr>\n      <td>layout-fill</td>\n      <td></td>\n    </tr>\n    <tr>\n      <td>layout-wrap</td>\n      <td></td>\n    </tr>\n    <tr>\n      <td>layout-nowrap</td>\n      <td></td>\n    </tr>\n    <tr>\n      <td>layout-margin</td>\n      <td></td>\n    </tr>\n    <tr>\n      <td>layout-padding</td>\n      <td></td>\n    </tr>\n    <tr>\n      <td>show</td>\n      <td></td>\n    </tr>\n    <tr>\n      <td>hide</td>\n      <td></td>\n    </tr>\n    </tbody>\n  </table>\n\n\n  <p>And if we use Breakpoints as specified in Material Design:</p>\n  <p><a\n      href="https://camo.githubusercontent.com/ad81ae92f8b4285747ce4e48007bf3f104dd5630/687474703a2f2f6d6174657269616c2d64657369676e2e73746f726167652e676f6f676c65617069732e636f6d2f7075626c6973682f6d6174657269616c5f765f342f6d6174657269616c5f6578745f7075626c6973682f3042386f6c5631354a3761625053474678656d46695156527462316b2f6c61796f75745f61646170746976655f627265616b706f696e74735f30312e706e67"\n      target="_blank"><img\n      src="https://camo.githubusercontent.com/ad81ae92f8b4285747ce4e48007bf3f104dd5630/687474703a2f2f6d6174657269616c2d64657369676e2e73746f726167652e676f6f676c65617069732e636f6d2f7075626c6973682f6d6174657269616c5f765f342f6d6174657269616c5f6578745f7075626c6973682f3042386f6c5631354a3761625053474678656d46695156527462316b2f6c61796f75745f61646170746976655f627265616b706f696e74735f30312e706e67"\n      alt=""\n      style="max-width:100%;"></a>\n  </p>\n\n\n  <p>We can associate breakpoints with mediaQuery definitions using breakpoint <strong>alias(es)</strong>:</p>\n\n  <table class="md-api-table">\n    <thead>\n    <tr>\n      <th>Breakpoint</th>\n      <th>MediaQuery (pixel range)</th>\n    </tr>\n    </thead>\n    <tbody>\n    <tr>\n      <td>xs</td>\n      <td>\'(max-width: <b>599</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>gt-xs</td>\n      <td>\'(min-width: <b>600</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>sm</td>\n      <td>\'(min-width: <b>600</b>px) and (max-width: <b>959</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>gt-sm</td>\n      <td>\'(min-width: <b>960</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>md</td>\n      <td>\'(min-width: <b>960</b>px) and (max-width: <b>1279</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>gt-md</td>\n      <td>\'(min-width: <b>1280</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>lg</td>\n      <td>\'(min-width: <b>1280</b>px) and (max-width: <b>1919</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>gt-lg</td>\n      <td>\'(min-width: <b>1920</b>px)\'</td>\n    </tr>\n    <tr>\n      <td>xl</td>\n      <td>\'(min-width: <b>1920</b>px)\'</td>\n    </tr>\n    </tbody>\n  </table>\n\n  <br/>\n  <hr>\n  <h3>\n    API with Responsive Breakpoints\n  </h3>\n\n  <p>Now we can combine the breakpoint <code>alias</code> with the Layout API to easily support Responsive breakpoints\n    with our simple Layout markup convention. The <code>alias</code> is simply used as <b>suffix</b> extensions to the Layout\n    API keyword.\n    <br/>e.g.\n  </p>\n\n  <p>\n    This notation results in, for example, the following table for the <code>layout</code> and <code>flex</code> APIs:\n  </p>\n\n  <table class="md-api-table">\n      <thead>\n      <tr>\n        <th>layout API</th>\n        <th>flex API</th>\n        <th>Activates when device</th>\n      </tr>\n      </thead>\n      <tr>\n        <td>layout</td>\n        <td>flex</td>\n        <td>Sets default layout direction &amp; flex unless overridden by another breakpoint.</td>\n      </tr>\n      <tr>\n        <td>layout-xs</td>\n        <td>flex-xs</td>\n        <td>width &lt; <b>600</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-gt-xs</td>\n        <td>flex-gt-xs</td>\n        <td>width &gt;= <b>600</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-sm</td>\n        <td>flex-sm</td>\n        <td><b>600</b>px &lt;= width &lt; <b>960</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-gt-sm</td>\n        <td>flex-gt-sm</td>\n        <td>width &gt;= <b>960</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-md</td>\n        <td>flex-md</td>\n        <td><b>960</b>px &lt;= width &lt; <b>1280</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-gt-md</td>\n        <td>flex-gt-md</td>\n        <td>width &gt;= <b>1280</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-lg</td>\n        <td>flex-lg</td>\n        <td><b>1280</b>px &lt;= width &lt; <b>1920</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-gt-lg</td>\n        <td>flex-gt-lg</td>\n        <td>width &gt;= <b>1920</b>px</td>\n      </tr>\n      <tr>\n        <td>layout-xl</td>\n        <td>flex-xl</td>\n        <td>width &gt;= <b>1920</b>px</td>\n      </tr>\n    </table>\n\n  <p>Below is an example usage of the Responsive Layout API:</p>\n\n  <hljs lang="html">\n    <div layout=\'column\' class="zero">\n\n      <div flex="33" flex-md="{{ vm.box1Width }}" class="one"></div>\n      <div flex="33" layout="{{ vm.direction }}" layout-md="row" class="two">\n\n        <div flex="20" flex-md="10" hide-lg class="two_one"></div>\n        <div flex="30px" show hide-md="{{ vm.hideBox }}" flex-md="25" class="two_two"></div>\n        <div flex="20" flex-md="65" class="two_three"></div>\n\n      </div>\n      <div flex class="three"></div>\n\n    </div>\n  </hljs>\n\n  <br/>\n\n  <p>\n  This Layout API means it is much easier to set up and maintain flexbox layouts vs. defining everything via CSS.\n  The developer uses the Layout HTML API to specify <b><i>intention</i></b> and the Layout engine handles all the issues of CSS flexbox styling.\n  </p>\n\n  <p class="layout_note">\n    The Layout engine will log console warnings when it encounters conflicts or known issues.\n  </p>\n\n\n  <br/><br/>\n  <hr>\n  <br/>\n\n  <h3>Under-the-Hood</h3>\n\n  <p>\n    Due to performance problems when using Attribute Selectors with <b>Internet Explorer</b> browsers; see the following for more details:\n  </p>\n\n  <p>\n    Layout directives dynamically generate class selectors at runtime. And the Layout CSS classNames and styles have each been\n    predefined in the <code>angular-material.css</code> stylesheet.\n  </p>\n\n  <p class="layout_note">\n    Developers should continue to use Layout directives in the HTML\n    markup as the classes may change between releases.\n  </p>\n\n  <p>\n    Let\'s see how this directive-to-className transformation works. Consider the simple use of the <code>layout</code> and <code>flex</code> directives (API):\n  </p>\n\n  <hljs lang="html">\n    <div>\n\n      <div layout="row">\n\n        <div flex>First item in row</div>\n        <div flex="20">Second item in row</div>\n\n      </div>\n      <div layout="column">\n\n        <div flex="66">First item in column</div>\n        <div flex="33">Second item in column</div>\n\n      </div>\n\n    </div>\n  </hljs>\n\n\n  <p>\n    At runtime, these attributes are transformed to CSS classes.\n  </p>\n\n  <hljs lang="html">\n    <div>\n\n      <div class="ng-scope layout-row">\n\n        <div class="flex">First item in row</div>\n        <div class="flex-20">Second item in row</div>\n\n      </div>\n      <div class="ng-scope layout-column">\n\n        <div class="flex-33">First item in column</div>\n        <div class="flex-66">Second item in column</div>\n\n      </div>\n\n    </div>\n  </hljs>\n\n  <p>\n    Using the style classes above defined in <code>angular-material.css</code>\n  </p>\n\n  <hljs lang="css">\n\n    .flex {\n      -webkit-flex: 1 1 0%;\n          -ms-flex: 1 1 0%;\n              flex: 1 1 0%;\n      box-sizing: border-box;\n    }\n    .flex-20 {\n      -webkit-flex: 1 1 20%;\n          -ms-flex: 1 1 20%;\n              flex: 1 1 20%;\n      max-width: 20%;\n      max-height: 100%;\n      box-sizing: border-box;\n    }\n\n    .layout-row .flex-33 {\n      -webkit-flex: 1 1 calc(100% / 3);\n          -ms-flex: 1 1 calc(100% / 3);\n              flex: 1 1 calc(100% / 3);\n      box-sizing: border-box;\n    }\n\n    .layout-row  .flex-66 {\n      -webkit-flex: 1 1 calc(200% / 3);\n          -ms-flex: 1 1 calc(200% / 3);\n              flex: 1 1 calc(200% / 3);\n      box-sizing: border-box;\n    }\n\n\n    .layout-row .flex-33 {\n      max-width: calc(100% / 3);\n      max-height: 100%;\n    }\n\n    .layout-row  .flex-66 {\n      max-width: calc(200% / 3);\n      max-height: 100%;\n    }\n\n    .layout-column .flex-33 {\n      max-width: 100%;\n      max-height: calc(100% / 3);\n    }\n\n    .layout-column  .flex-66 {\n      max-width: 100%;\n      max-height: calc(200% / 3);\n    }\n  </hljs>\n\n</div>\n');
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/layout-options.tmpl.html", '<div ng-controller="LayoutCtrl" class="layout-content layout-options" ng-cloak>\n\n  <docs-demo demo-title="Responsive Layout" class="small-demo colorNested">\n    <demo-file name="index.html">\n      <div layout="row" layout-sm="column">\n        <div flex>\n          I\'m above on mobile, and to the left on larger devices.\n        </div>\n        <div flex>\n          I\'m below on mobile, and to the right on larger devices.\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <p>\n    See the <a href="layout/container">Container Elements</a> page for a basic explanation\n    of layout directives.\n    <br/>\n    To make your layout change depending upon the device screen size, there are\n    other <code>layout</code> directives available:\n  </p>\n\n  <table class="md-api-table">\n    <thead>\n    <tr>\n      <th>API</th>\n      <th>Activates when device</th>\n    </tr>\n    </thead>\n    <tr>\n      <td>layout</td>\n      <td>Sets default layout direction unless overridden by another breakpoint.</td>\n    </tr>\n    <tr>\n      <td>layout-xs</td>\n      <td>width &lt; <b>600</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-gt-xs</td>\n      <td>width &gt;= <b>600</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-sm</td>\n      <td><b>600</b>px &lt;= width &lt; <b>960</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-gt-sm</td>\n      <td>width &gt;= <b>960</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-md</td>\n      <td><b>960</b>px &lt;= width &lt; <b>1280</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-gt-md</td>\n      <td>width &gt;= <b>1280</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-lg</td>\n      <td><b>1280</b>px &lt;= width &lt; <b>1920</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-gt-lg</td>\n      <td>width &gt;= <b>1920</b>px</td>\n    </tr>\n    <tr>\n      <td>layout-xl</td>\n      <td>width &gt;= <b>1920</b>px</td>\n    </tr>\n  </table>\n  <br/>\n\n  <br/>\n  <hr>\n  <br/>\n\n  <h3>Layout Margin, Padding, Wrap and Fill</h3>\n  <br/>\n\n\n  <docs-demo demo-title="Layout Margin, Padding, and Fill" class="small-demo colorNested-noPad">\n    <demo-file name="index.html">\n      <div layout="row" layout-margin>\n        <div flex>Parent layout and this element have margins.</div>\n      </div>\n      <div layout="row" layout-padding>\n        <div flex>Parent layout and this element have padding.</div>\n      </div>\n      <div layout="row" layout-fill style="min-height: 224px;">\n        <div flex>Parent layout is set to fill available space.</div>\n      </div>\n      <div layout="row" layout-padding layout-margin layout-fill style="min-height: 224px;">\n        <div flex>I am using all three at once.</div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <p>\n    <code>layout-margin</code> adds margin around each <code>flex</code> child. It also adds a margin to the layout\n    container itself.\n    <br/>\n    <code>layout-padding</code> adds padding inside each <code>flex</code> child. It also adds padding to the layout\n    container itself.\n    <br/>\n    <code>layout-fill</code> forces the layout element to fill its parent container.\n  </p>\n\n\n  <p>Here is a non-trivial group of <code>flex</code> elements using <code>layout-wrap</code></p>\n\n  <docs-demo demo-title="Wrap" class="small-demo colorNested-noPad">\n    <demo-file name="index.html">\n      <div layout="row" layout-wrap>\n        <div flex="33">[flex=33]</div>\n        <div flex="66">[flex=66]</div>\n        <div flex="66">[flex=66]</div>\n        <div flex="33">[flex=33]</div>\n        <div flex="33">[flex=33]</div>\n        <div flex="33">[flex=33]</div>\n        <div flex="33">[flex=33]</div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <p>\n    <code>layout-wrap</code> allows <code>flex</code> children to wrap within the container if the elements use more\n    than 100%.\n    <br/>\n  </p>\n\n  <br/>\n\n  <br/>\n    <hr>\n    <br/>\n\n    <h3>Show &amp; Hide </h3>\n\n  <p>Use the <code>show</code> <code>hide</code> APIs to responsively show or hide elements. While these work similar\n  to <code>ng-show</code> and <code>ng-hide</code>, these AngularJS Material Layout directives are mediaQuery-aware.\n  </p>\n\n  <docs-demo demo-title="Hide and Show Directives" class="small-demo colorNested">\n    <demo-file name="index.html">\n      <div layout="row">\n        <div hide show-gt-sm flex>\n          Only show on gt-sm devices.\n        </div>\n        <div hide-gt-sm flex>\n          Shown on small and medium.<br/>\n          Hidden on gt-sm devices.\n        </div>\n        <div show hide-gt-md flex>\n          Shown on small and medium size devices.<br/>\n          Hidden on gt-md devices.\n        </div>\n        <div hide show-md flex>\n          Shown on medium size devices only.\n        </div>\n        <div hide show-gt-lg flex>\n          Shown on devices larger than 1200px wide only.\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n  <br/>\n  <table class="md-api-table">\n    <thead>\n      <tr>\n        <th>hide (display: none)</th>\n        <th>show (negates hide)</th>\n        <th>Activates when:</th>\n      </tr>\n    </thead>\n    <tr>\n      <td>hide-xs</td>\n      <td>show-xs</td>\n      <td>width &lt; <b>600</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-gt-xs</td>\n      <td>show-gt-xs</td>\n      <td>width &gt;= <b>600</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-sm</td>\n      <td>show-sm</td>\n      <td><b>600</b>px &lt;= width &lt; <b>960</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-gt-sm</td>\n      <td>show-gt-sm</td>\n      <td>width &gt;= <b>960</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-md</td>\n      <td>show-md</td>\n      <td><b>960</b>px &lt;= width &lt; <b>1280</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-gt-md</td>\n      <td>show-gt-md</td>\n      <td>width &gt;= <b>1280</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-lg</td>\n      <td>show-lg</td>\n      <td><b>1280</b>px &lt;= width &lt; <b>1920</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-gt-lg</td>\n      <td>show-gt-lg</td>\n      <td>width &gt;= <b>1920</b>px</td>\n    </tr>\n    <tr>\n      <td>hide-xl</td>\n      <td>show-xl</td>\n      <td>width &gt;= <b>1920</b>px</td>\n    </tr>\n  </table>\n\n\n</div>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/layout-tips.tmpl.html", '<style>\n  ul.spaced li {\n    margin-bottom: 15px;\n  }\n</style>\n<div ng-controller="LayoutTipsCtrl as tips" class="layout-content">\n  <h3>Overview</h3>\n\n  <p>\n    The AngularJS Material Layout system uses the current\n    <a href="http://www.w3.org/TR/css3-flexbox/">Flexbox</a> feature set. More importantly, it also\n    adds syntactic sugar to allow developers to easily and quickly add Responsive features to HTML\n    containers and elements.\n  </p>\n\n  <p>\n    As you use the Layout features, you may encounter scenarios where the layouts do not render as\n    expected; especially with IE 10 and 11 browsers. There may also be cases where you need to add\n    custom HTML, CSS and Javascript to achieve your desired results.\n  </p>\n\n\n  <br/>\n  <hr/>\n\n  <h3>Resources</h3>\n\n  <p>\n    If you are experiencing an issue in a particular browser, we highly recommend using the\n    following resources for known issues and workarounds.\n  </p>\n\n  <ul>\n    <li><a href="https://github.com/philipwalton/flexbugs#flexbugs" target="_blank">FlexBugs</a></li>\n    <li><a href="https://philipwalton.github.io/solved-by-flexbox/" target="_blank">Solved by FlexBugs</a></li>\n    <li><a href="http://philipwalton.com/articles/normalizing-cross-browser-flexbox-bugs/" target="_blank">Normalizing Cross-browser Flexbox Bugs</a></li>\n    <li style="margin-bottom: 20px;"><a href="http://caniuse.com/#search=flex" target="_blank">Can I use flexbox...? ( see the <code>Known Issues</code> tab)</a></li>\n    <li><a href="https://groups.google.com/forum/#!forum/ngmaterial">AngularJS Material Forum</a></li>\n    <li style="margin-top: 20px;"><a href="https://css-tricks.com/snippets/css/a-guide-to-flexbox/" target="_blank">A Complete Guide to Flexbox</a></li>\n    <li style="margin-bottom: 20px;"><a href="https://scotch.io/tutorials/a-visual-guide-to-css3-flexbox-properties" target="_blank">A Visual Guide to CSS3 Flexbox Properties</a></li>\n  </ul>\n\n\n  <br/>\n  <hr/>\n\n  <h3>General Tips</h3>\n\n  <p>\n    Below, you will find solutions to some of the more common scenarios and problems that may arise\n    when using Material\'s Layout system. The following sections offer general guidelines and tips when using the <code>flex</code> and\n        <code>layout</code> directives within your own applications.\n  </p>\n\n  <ul class="spaced">\n    <li>\n      When building your application\'s Layout, it is usually best to start with a mobile version\n      that looks and works correctly, and then apply styling for larger devices using the\n      <code>flex-gt-*</code> or <code>hide-gt-*</code> attributes. This approach typically leads\n      to less frustration than starting big and attempting to fix issues on smaller devices.\n    </li>\n\n    <li>\n      Some elements like <code>&lt;fieldset&gt;</code> and <code>&lt;button&gt;</code> do not always\n      work correctly with flex. Additionally, some of the AngularJS Material components provide their\n      own styles. If you are having difficulty with a specific element/component, but not\n      others, try applying the flex attributes to a parent or child <code>&lt;div&gt;</code> of the\n      element instead.\n    </li>\n\n    <li>\n      Some Flexbox properties such as <code>flex-direction</code> <u>cannot</u> be animated.\n    </li>\n\n    <li>\n      Flexbox can behave differently on different browsers. You should test as many as possible on\n      a regular basis so that you can catch and fix layout issues more quickly.\n    </li>\n  </ul>\n\n  <br/>\n  <hr/>\n\n  <h3>Layout Column</h3>\n\n  <p>\n    In some scenarios <code>layout="column"</code> and breakpoints (xs, gt-xs, xs, gt-sm, etc.) may not work\n    as expected due to CSS specificity rules.\n  </p>\n\n  <div class="md-whiteframe-3dp">\n\n    <iframe height=\'700\' scrolling=\'no\'\n            src=\'//codepen.io/team/AngularMaterial/embed/obgapg/?height=700&theme-id=21180&default-tab=result\'\n            frameborder=\'no\' allowtransparency=\'true\' allowfullscreen=\'true\' style=\'width: 100%;\'>See the Pen <a\n        href=\'http://codepen.io/team/AngularMaterial/pen/obgapg/\'>Card Layouts (corrected)</a> by AngularJS Material (<a\n        href=\'http://codepen.io/AngularMaterial\'>@AngularMaterial</a>) on <a href=\'http://codepen.io\'>CodePen</a>.\n    </iframe>\n\n  </div>\n\n    <p>\n      This is easily fixed simply by inverting the layout logic so that the default is <code>layout=\'row\'</code>.\n      To see how the layout changes, shrink the browser window its width is <600px;\n    </p>\n\n\n  <br/>\n  <hr/>\n\n  <h3 id="layout-column-0px-ie11">IE11 - Layout Column, 0px Height</h3>\n\n  <p>\n    In Internet Explorer 11, when you have a column layout with unspecified height and flex items\n    inside, the browser can get confused and incorrectly calculate the height of each item (and thus\n    the container) as <code>0px</code>, making the items overlap and not take up the proper amount\n    of space.\n  </p>\n\n  <p class="layout_note">\n    <b>Note:</b> The flex items below actually do have some height. This is because our doc-specific\n    CSS provides a small bit of padding (<code>8px</code>). We felt that the extra padding made for\n    a better demo of the actual issue.\n  </p>\n\n  <docs-demo demo-title="IE11 - Layout Column, 0px Height" class="colorNested">\n    <demo-file name="index.html">\n      <div flex layout="column">\n        <div flex>\n          11111<br />11111<br />11111\n        </div>\n\n        <div flex>\n          22222<br />22222<br />22222\n        </div>\n\n        <div flex>\n          33333<br />33333<br />33333\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <p>\n    Unfortunately, there is no IE11 specific fix available, and the suggested workaround is to set\n    the <code>flex-basis</code> property to <code>auto</code> instead of <code>0px</code> (which is\n    the default setting).\n  </p>\n\n  <p>\n    You can easily achieve this using the <code>flex="auto"</code> attribute that the Layout system\n    provides.\n  </p>\n\n  <docs-demo demo-title="IE11 - Layout Column, 0px Height (Fix 1)" class="colorNested">\n    <demo-file name="index.html">\n      <div flex layout="column">\n        <div flex="auto">\n          11111<br />11111<br />11111\n        </div>\n\n        <div flex="auto">\n          22222<br />22222<br />22222\n        </div>\n\n        <div flex="auto">\n          33333<br />33333<br />33333\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n\n  <p>\n    Alternatively, you can manually set the height of the layout container (<code>400px</code>\n    in the demo below).\n  </p>\n\n  <docs-demo demo-title="IE11 - Layout Column, 0px Height (Fix 2)" class="colorNested">\n    <demo-file name="index.html">\n      <div flex layout="column" style="height: 400px;">\n        <div flex>\n          11111<br />11111<br />11111\n        </div>\n\n        <div flex>\n          22222<br />22222<br />22222\n        </div>\n\n        <div flex>\n          33333<br />33333<br />33333\n        </div>\n      </div>\n    </demo-file>\n  </docs-demo>\n\n  <br/>\n  <hr/>\n\n  <h3>Flex Element Heights</h3>\n\n  <p>\n    Firefox currently has an issue calculating the proper height of flex containers whose children\n    are flex, but have more content than can properly fit within the container.\n  </p>\n\n  <p>\n    This is particularly problematic if the <code>flex</code> children are <code>md-content</code> components as\n    it will prevent the content from scrolling correctly, instead scrolling the entire body.\n  </p>\n\n  <div class="md-whiteframe-3dp">\n' + "    <iframe height='376' scrolling='no'\n            src='//codepen.io/team/AngularMaterial/embed/NxKBwW/?height=376&theme-id=0&default-tab=result'\n            frameborder='no' allowtransparency='true' allowfullscreen='true' style='width: 100%;'>\n      See the Pen <a href='http://codepen.io/team/AngularMaterial/pen/NxKBwW/'>AngularJS Material Basic App</a>\n      by AngularJS Material (<a href='http://codepen.io/AngularMaterial'>@AngularJSMaterial</a>)\n      on <a href='http://codepen.io'>CodePen</a>.\n    </iframe>\n  </div>\n\n  <p>\n    Notice in the above Codepen how we must set <code>overflow: auto</code> on the div with the\n    <code>change-my-css</code> class in order for Firefox to properly calculate the height and\n    shrink to the available space.\n  </p>\n\n</div>\n")
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/license.tmpl.html", '<div ng-controller="GuideCtrl" class="doc-content">\n  <md-content>\n    <p>The MIT License</p>\n\n    <p>\n      Copyright (c) 2014-2016 Google, Inc.\n      <a href="http://angularjs.org">http://angularjs.org</a>\n    </p>\n\n    <p>\n      Permission is hereby granted, free of charge, to any person obtaining a copy\n      of this software and associated documentation files (the "Software"), to deal\n      in the Software without restriction, including without limitation the rights\n      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n      copies of the Software, and to permit persons to whom the Software is\n      furnished to do so, subject to the following conditions:\n    </p>\n\n    <p>\n      The above copyright notice and this permission notice shall be included in\n      all copies or substantial portions of the Software.\n    </p>\n\n    <p>\n      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\n      THE SOFTWARE.\n    </p>\n  </md-content>\n</div>')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/menu-link.tmpl.html", '<md-button\n    ng-class="{\'active\' : isSelected()}"\n    ng-href="{{section.url}}"\n    ng-click="focusSection()">\n  {{section | humanizeDoc}}\n  <span class="md-visually-hidden"\n    ng-if="isSelected()">\n    current page\n  </span>\n</md-button>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/menu-toggle.tmpl.html", '<md-button class="md-button-toggle"\n  ng-click="toggle()"\n  aria-controls="docs-menu-{{section.name | nospace}}"\n  aria-expanded="{{isOpen()}}">\n  <div flex layout="row">\n    {{section.name}}\n    <span flex></span>\n    <span aria-hidden="true" class="md-toggle-icon"\n    ng-class="{\'toggled\' : isOpen()}">\n      <md-icon md-svg-icon="md-toggle-arrow"></md-icon>\n    </span>\n  </div>\n  <span class="md-visually-hidden">\n    Toggle {{isOpen()? \'expanded\' : \'collapsed\'}}\n  </span>\n</md-button>\n\n<ul id="docs-menu-{{section.name | nospace}}"\n  class="menu-toggle-list"\n  aria-hidden="{{!renderContent}}"\n  ng-style="{ visibility: renderContent ? \'visible\' : \'hidden\' }">\n\n  <li ng-repeat="page in section.pages">\n    <menu-link section="page"></menu-link>\n  </li>\n</ul>\n')
}]), angular.module("docsApp").run(["$templateCache", function (e) {
	e.put("partials/view-source.tmpl.html", '<md-dialog class="view-source-dialog">\n\n  <md-tabs>\n    <md-tab ng-repeat="file in files"\n                  active="file === data.selectedFile"\n                  ng-click="data.selectedFile = file" >\n        <span class="window_label">{{file.viewType}}</span>\n    </md-tab>\n  </md-tabs>\n\n  <md-dialog-content md-scroll-y flex>\n    <div ng-repeat="file in files">\n      <hljs code="file.content"\n        lang="{{file.fileType}}"\n        ng-show="file === data.selectedFile" >\n      </hljs>\n    </div>\n  </md-dialog-content>\n\n  <md-dialog-actions layout="horizontal">\n    <md-button class="md-primary" ng-click="$hideDialog()">\n      Done\n    </md-button>\n  </md-dialog-actions>\n</md-dialog>\n')
}]), angular.module("docsApp").directive("hljs", ["$timeout", "$q", "$interpolate", function (e, t, a) {
	return {
		restrict: "E", compile: function (n, o) {
			var l;
			return o.code || (l = n.html(), n.empty()), function (n, o, i) {
				function s(e, t) {
					var a = t.find("code"), n = e.replace(/\n{2,}/g, "\n\n").replace(/^\n/, "").replace(/\n$/, ""), o = n.split("\n"), l = o[0].match(/^\s*/)[0], s = new RegExp("^" + l);
					o = o.map(function (e) {
						return e.replace(s, "").replace(/\s+$/, "")
					});
					var r = hljs.highlight(i.language || i.lang, o.join("\n"), !0);
					r.value = r.value.replace(/=<span class="hljs-value">""<\/span>/gi, "").replace("<head>", "").replace("<head/>", ""), a.append(r.value).addClass("highlight")
				}

				i.code && (l = n.$eval(i.code));
				var r = n.$eval(i.shouldInterpolate);
				t.when(l).then(function (t) {
					if (t) {
						r && (t = a(t)(n));
						var l = angular.element('<pre><code class="highlight" ng-non-bindable></code></pre>');
						o.append(l), e(function () {
							s(t, l)
						}, 34, !1)
					}
				})
			}
		}
	}
}]);
var hljs = new function () {
	function e(e) {
		return e.replace(/&/gm, "&amp;").replace(/</gm, "&lt;").replace(/>/gm, "&gt;")
	}

	function t(e) {
		return e.nodeName.toLowerCase()
	}

	function a(e, t) {
		var a = e && e.exec(t);
		return a && 0 == a.index
	}

	function n(e) {
		var t = (e.className + " " + (e.parentNode ? e.parentNode.className : "")).split(/\s+/);
		return t = t.map(function (e) {
			return e.replace(/^lang(uage)?-/, "")
		}), t.filter(function (e) {
			return f(e) || "no-highlight" == e
		})[0]
	}

	function o(e, t) {
		var a = {};
		for (var n in e)a[n] = e[n];
		if (t)for (var n in t)a[n] = t[n];
		return a
	}

	function l(e) {
		var a = [];
		return function n(e, o) {
			for (var l = e.firstChild; l; l = l.nextSibling)3 == l.nodeType ? o += l.nodeValue.length : "br" == t(l) ? o += 1 : 1 == l.nodeType && (a.push({
				event: "start",
				offset: o,
				node: l
			}), o = n(l, o), a.push({event: "stop", offset: o, node: l}));
			return o
		}(e, 0), a
	}

	function i(a, n, o) {
		function l() {
			return a.length && n.length ? a[0].offset != n[0].offset ? a[0].offset < n[0].offset ? a : n : "start" == n[0].event ? a : n : a.length ? a : n
		}

		function i(a) {
			function n(t) {
				return " " + t.nodeName + '="' + e(t.value) + '"'
			}

			d += "<" + t(a) + Array.prototype.map.call(a.attributes, n).join("") + ">"
		}

		function s(e) {
			d += "</" + t(e) + ">"
		}

		function r(e) {
			("start" == e.event ? i : s)(e.node)
		}

		for (var m = 0, d = "", c = []; a.length || n.length;) {
			var p = l();
			if (d += e(o.substr(m, p[0].offset - m)), m = p[0].offset, p == a) {
				c.reverse().forEach(s);
				do r(p.splice(0, 1)[0]), p = l(); while (p == a && p.length && p[0].offset == m);
				c.reverse().forEach(i)
			} else"start" == p[0].event ? c.push(p[0].node) : c.pop(), r(p.splice(0, 1)[0])
		}
		return d + e(o.substr(m))
	}

	function s(e) {
		function t(e) {
			return e && e.source || e
		}

		function a(a, n) {
			return RegExp(t(a), "m" + (e.cI ? "i" : "") + (n ? "g" : ""))
		}

		function n(l, i) {
			if (!l.compiled) {
				if (l.compiled = !0, l.k = l.k || l.bK, l.k) {
					var s = {}, r = function (t, a) {
						e.cI && (a = a.toLowerCase()), a.split(" ").forEach(function (e) {
							var a = e.split("|");
							s[a[0]] = [t, a[1] ? Number(a[1]) : 1]
						})
					};
					"string" == typeof l.k ? r("keyword", l.k) : Object.keys(l.k).forEach(function (e) {
						r(e, l.k[e])
					}), l.k = s
				}
				l.lR = a(l.l || /\b[A-Za-z0-9_]+\b/, !0), i && (l.bK && (l.b = "\\b(" + l.bK.split(" ").join("|") + ")\\b"), l.b || (l.b = /\B|\b/), l.bR = a(l.b), l.e || l.eW || (l.e = /\B|\b/), l.e && (l.eR = a(l.e)), l.tE = t(l.e) || "", l.eW && i.tE && (l.tE += (l.e ? "|" : "") + i.tE)), l.i && (l.iR = a(l.i)), void 0 === l.r && (l.r = 1), l.c || (l.c = []);
				var m = [];
				l.c.forEach(function (e) {
					e.v ? e.v.forEach(function (t) {
						m.push(o(e, t))
					}) : m.push("self" == e ? l : e)
				}), l.c = m, l.c.forEach(function (e) {
					n(e, l)
				}), l.starts && n(l.starts, i);
				var d = l.c.map(function (e) {
					return e.bK ? "\\.?(" + e.b + ")\\.?" : e.b
				}).concat([l.tE, l.i]).map(t).filter(Boolean);
				l.t = d.length ? a(d.join("|"), !0) : {
					exec: function (e) {
						return null
					}
				}, l.continuation = {}
			}
		}

		n(e)
	}

	function r(t, n, o, l) {
		function i(e, t) {
			for (var n = 0; n < t.c.length; n++)if (a(t.c[n].bR, e))return t.c[n]
		}

		function d(e, t) {
			return a(e.eR, t) ? e : e.eW ? d(e.parent, t) : void 0
		}

		function c(e, t) {
			return !o && a(t.iR, e)
		}

		function p(e, t) {
			var a = j.cI ? t[0].toLowerCase() : t[0];
			return e.k.hasOwnProperty(a) && e.k[a]
		}

		function u(e, t, a, n) {
			var o = n ? "" : y.classPrefix, l = '<span class="' + o, i = a ? "" : "</span>";
			return l += e + '">', l + t + i
		}

		function h() {
			if (!P.k)return e(S);
			var t = "", a = 0;
			P.lR.lastIndex = 0;
			for (var n = P.lR.exec(S); n;) {
				t += e(S.substr(a, n.index - a));
				var o = p(P, n);
				o ? (D += o[1], t += u(o[0], e(n[0]))) : t += e(n[0]), a = P.lR.lastIndex, n = P.lR.exec(S)
			}
			return t + e(S.substr(a))
		}

		function b() {
			if (P.sL && !v[P.sL])return e(S);
			var t = P.sL ? r(P.sL, S, !0, P.continuation.top) : m(S);
			return P.r > 0 && (D += t.r), "continuous" == P.subLanguageMode && (P.continuation.top = t.top), u(t.language, t.value, !1, !0)
		}

		function g() {
			return void 0 !== P.sL ? b() : h()
		}

		function x(t, a) {
			var n = t.cN ? u(t.cN, "", !0) : "";
			t.rB ? (T += n, S = "") : t.eB ? (T += e(a) + n, S = "") : (T += n, S = a), P = Object.create(t, {parent: {value: P}})
		}

		function w(t, a) {
			if (S += t, void 0 === a)return T += g(), 0;
			var n = i(a, P);
			if (n)return T += g(), x(n, a), n.rB ? 0 : a.length;
			var o = d(P, a);
			if (o) {
				var l = P;
				l.rE || l.eE || (S += a), T += g();
				do P.cN && (T += "</span>"), D += P.r, P = P.parent; while (P != o.parent);
				return l.eE && (T += e(a)), S = "", o.starts && x(o.starts, ""), l.rE ? 0 : a.length
			}
			if (c(a, P))throw new Error('Illegal lexeme "' + a + '" for mode "' + (P.cN || "<unnamed>") + '"');
			return S += a, a.length || 1
		}

		var j = f(t);
		if (!j)throw new Error('Unknown language: "' + t + '"');
		s(j);
		for (var P = l || j, T = "", C = P; C != j; C = C.parent)C.cN && (T += u(C.cN, T, !0));
		var S = "", D = 0;
		try {
			for (var U, M, B = 0; ;) {
				if (P.t.lastIndex = B, U = P.t.exec(n), !U)break;
				M = w(n.substr(B, U.index - B), U[0]), B = U.index + M
			}
			w(n.substr(B));
			for (var C = P; C.parent; C = C.parent)C.cN && (T += "</span>");
			return {r: D, value: T, language: t, top: P}
		} catch (k) {
			if (k.message.indexOf("Illegal") != -1)return {r: 0, value: e(n)};
			throw k
		}
	}

	function m(t, a) {
		a = a || y.languages || Object.keys(v);
		var n = {r: 0, value: e(t)}, o = n;
		return a.forEach(function (e) {
			if (f(e)) {
				var a = r(e, t, !1);
				a.language = e, a.r > o.r && (o = a), a.r > n.r && (o = n, n = a)
			}
		}), o.language && (n.second_best = o), n
	}

	function d(e) {
		return y.tabReplace && (e = e.replace(/^((<[^>]+>|\t)+)/gm, function (e, t, a, n) {
			return t.replace(/\t/g, y.tabReplace)
		})), y.useBR && (e = e.replace(/\n/g, "<br>")), e
	}

	function c(e) {
		var t = y.useBR ? e.innerHTML.replace(/\n/g, "").replace(/<br>|<br [^>]*>/g, "\n").replace(/<[^>]*>/g, "") : e.textContent, a = n(e);
		if ("no-highlight" != a) {
			var o = a ? r(a, t, !0) : m(t), s = l(e);
			if (s.length) {
				var c = document.createElementNS("http://www.w3.org/1999/xhtml", "pre");
				c.innerHTML = o.value, o.value = i(s, l(c), t)
			}
			o.value = d(o.value), e.innerHTML = o.value, e.className += " hljs " + (!a && o.language || ""), e.result = {
				language: o.language,
				re: o.r
			}, o.second_best && (e.second_best = {language: o.second_best.language, re: o.second_best.r})
		}
	}

	function p(e) {
		y = o(y, e)
	}

	function u() {
		if (!u.called) {
			u.called = !0;
			var e = document.querySelectorAll("pre code");
			Array.prototype.forEach.call(e, c)
		}
	}

	function h() {
		addEventListener("DOMContentLoaded", u, !1), addEventListener("load", u, !1)
	}

	function b(e, t) {
		var a = v[e] = t(this);
		a.aliases && a.aliases.forEach(function (t) {
			x[t] = e
		})
	}

	function g() {
		return Object.keys(v)
	}

	function f(e) {
		return v[e] || v[x[e]]
	}

	var y = {classPrefix: "hljs-", tabReplace: null, useBR: !1, languages: void 0}, v = {}, x = {};
	this.highlight = r, this.highlightAuto = m, this.fixMarkup = d, this.highlightBlock = c, this.configure = p, this.initHighlighting = u, this.initHighlightingOnLoad = h, this.registerLanguage = b, this.listLanguages = g, this.getLanguage = f, this.inherit = o, this.IR = "[a-zA-Z][a-zA-Z0-9_]*", this.UIR = "[a-zA-Z_][a-zA-Z0-9_]*", this.NR = "\\b\\d+(\\.\\d+)?", this.CNR = "(\\b0[xX][a-fA-F0-9]+|(\\b\\d+(\\.\\d*)?|\\.\\d+)([eE][-+]?\\d+)?)", this.BNR = "\\b(0b[01]+)", this.RSR = "!|!=|!==|%|%=|&|&&|&=|\\*|\\*=|\\+|\\+=|,|-|-=|/=|/|:|;|<<|<<=|<=|<|===|==|=|>>>=|>>=|>=|>>>|>>|>|\\?|\\[|\\{|\\(|\\^|\\^=|\\||\\|=|\\|\\||~", this.BE = {
		b: "\\\\[\\s\\S]",
		r: 0
	}, this.ASM = {cN: "string", b: "'", e: "'", i: "\\n", c: [this.BE]}, this.QSM = {
		cN: "string",
		b: '"',
		e: '"',
		i: "\\n",
		c: [this.BE]
	}, this.PWM = {b: /\b(a|an|the|are|I|I'm|isn't|don't|doesn't|won't|but|just|should|pretty|simply|enough|gonna|going|wtf|so|such)\b/}, this.CLCM = {
		cN: "comment",
		b: "//",
		e: "$",
		c: [this.PWM]
	}, this.CBCM = {cN: "comment", b: "/\\*", e: "\\*/", c: [this.PWM]}, this.HCM = {cN: "comment", b: "#", e: "$", c: [this.PWM]}, this.NM = {
		cN: "number",
		b: this.NR,
		r: 0
	}, this.CNM = {cN: "number", b: this.CNR, r: 0}, this.BNM = {cN: "number", b: this.BNR, r: 0}, this.CSSNM = {
		cN: "number",
		b: this.NR + "(%|em|ex|ch|rem|vw|vh|vmin|vmax|cm|mm|in|pt|pc|px|deg|grad|rad|turn|s|ms|Hz|kHz|dpi|dpcm|dppx)?",
		r: 0
	}, this.RM = {cN: "regexp", b: /\//, e: /\/[gim]*/, i: /\n/, c: [this.BE, {b: /\[/, e: /\]/, r: 0, c: [this.BE]}]}, this.TM = {
		cN: "title",
		b: this.IR,
		r: 0
	}, this.UTM = {cN: "title", b: this.UIR, r: 0}
};
hljs.registerLanguage("javascript", function (e) {
	return {
		aliases: ["js"],
		k: {
			keyword: "in if for while finally var new function do return void else break catch instanceof with throw case default try this switch continue typeof delete let yield const class",
			literal: "true false null undefined NaN Infinity",
			built_in: "eval isFinite isNaN parseFloat parseInt decodeURI decodeURIComponent encodeURI encodeURIComponent escape unescape Object Function Boolean Error EvalError InternalError RangeError ReferenceError StopIteration SyntaxError TypeError URIError Number Math Date String RegExp Array Float32Array Float64Array Int16Array Int32Array Int8Array Uint16Array Uint32Array Uint8Array Uint8ClampedArray ArrayBuffer DataView JSON Intl arguments require module console window document"
		},
		c: [{cN: "pi", b: /^\s*('|")use strict('|")/, r: 10}, e.ASM, e.QSM, e.CLCM, e.CBCM, e.CNM, {
			b: "(" + e.RSR + "|\\b(case|return|throw)\\b)\\s*",
			k: "return throw case",
			c: [e.CLCM, e.CBCM, e.RM, {b: /</, e: />;/, r: 0, sL: "xml"}],
			r: 0
		}, {
			cN: "function",
			bK: "function",
			e: /\{/,
			eE: !0,
			c: [e.inherit(e.TM, {b: /[A-Za-z$_][0-9A-Za-z$_]*/}), {cN: "params", b: /\(/, e: /\)/, c: [e.CLCM, e.CBCM], i: /["'\(]/}],
			i: /\[|%/
		}, {b: /\$[(.]/}, {b: "\\." + e.IR, r: 0}]
	}
}), hljs.registerLanguage("css", function (e) {
	var t = "[a-zA-Z-][a-zA-Z0-9_-]*", a = {cN: "function", b: t + "\\(", rB: !0, eE: !0, e: "\\("};
	return {
		cI: !0,
		i: "[=/|']",
		c: [e.CBCM, {cN: "id", b: "\\#[A-Za-z0-9_-]+"}, {cN: "class", b: "\\.[A-Za-z0-9_-]+", r: 0}, {
			cN: "attr_selector",
			b: "\\[",
			e: "\\]",
			i: "$"
		}, {cN: "pseudo", b: ":(:)?[a-zA-Z0-9\\_\\-\\+\\(\\)\\\"\\']+"}, {
			cN: "at_rule",
			b: "@(font-face|page)",
			l: "[a-z-]+",
			k: "font-face page"
		}, {cN: "at_rule", b: "@", e: "[{;]", c: [{cN: "keyword", b: /\S+/}, {b: /\s/, eW: !0, eE: !0, r: 0, c: [a, e.ASM, e.QSM, e.CSSNM]}]}, {
			cN: "tag",
			b: t,
			r: 0
		}, {
			cN: "rules",
			b: "{",
			e: "}",
			i: "[^\\s]",
			r: 0,
			c: [e.CBCM, {
				cN: "rule",
				b: "[^\\s]",
				rB: !0,
				e: ";",
				eW: !0,
				c: [{
					cN: "attribute",
					b: "[A-Z\\_\\.\\-]+",
					e: ":",
					eE: !0,
					i: "[^\\s]",
					starts: {
						cN: "value",
						eW: !0,
						eE: !0,
						c: [a, e.CSSNM, e.QSM, e.ASM, e.CBCM, {cN: "hexcolor", b: "#[0-9A-Fa-f]+"}, {cN: "important", b: "!important"}]
					}
				}]
			}]
		}]
	}
}), hljs.registerLanguage("xml", function (e) {
	var t = "[A-Za-z0-9\\._:-]+", a = {b: /<\?(php)?(?!\w)/, e: /\?>/, sL: "php", subLanguageMode: "continuous"}, n = {
		eW: !0,
		i: /</,
		r: 0,
		c: [a, {cN: "attribute", b: t, r: 0}, {b: "=", r: 0, c: [{cN: "value", v: [{b: /"/, e: /"/}, {b: /'/, e: /'/}, {b: /[^\s\/>]+/}]}]}]
	};
	return {
		aliases: ["html", "xhtml", "rss", "atom", "xsl", "plist"],
		cI: !0,
		c: [{cN: "doctype", b: "<!DOCTYPE", e: ">", r: 10, c: [{b: "\\[", e: "\\]"}]}, {cN: "comment", b: "<!--", e: "-->", r: 10}, {
			cN: "cdata",
			b: "<\\!\\[CDATA\\[",
			e: "\\]\\]>",
			r: 10
		}, {cN: "tag", b: "<style(?=\\s|>|$)", e: ">", k: {title: "style"}, c: [n], starts: {e: "</style>", rE: !0, sL: "css"}}, {
			cN: "tag",
			b: "<script(?=\\s|>|$)",
			e: ">",
			k: {title: "script"},
			c: [n],
			starts: {e: "</script>", rE: !0, sL: "javascript"}
		}, {b: "<%", e: "%>", sL: "vbscript"}, a, {cN: "pi", b: /<\?\w+/, e: /\?>/, r: 10}, {
			cN: "tag",
			b: "</?",
			e: "/?>",
			c: [{cN: "title", b: "[^ /><]+", r: 0}, n]
		}]
	}
}), angular.module("docsApp").directive("ngPanel", ["$animate", function (e) {
	return {
		restrict: "EA", transclude: "element", terminal: !0, compile: function (t, a) {
			var n = a.ngPanel || a["for"], o = /^(\S+)(?:\s+track by (.+?))?$/, l = o.exec(n), i = !0, s = l[1], r = l[2];
			return r ? i = !1 : r = l[1], function (t, a, n, o, l) {
				var m, d;
				t[i ? "$watchCollection" : "$watch"](r, function (n) {
					m && e.leave(m), d && (d.$destroy(), d = null);
					i ? n : t.$eval(s);
					d = t.$new(), l(d, function (t) {
						m = t, e.enter(t, null, a)
					})
				})
			}
		}
	}
}]), function () {
	function e(e) {
		function t() {
			return n.map(a)
		}

		function a(t) {
			return "https://ajax.googleapis.com/ajax/libs/angularjs/" + e.ngVersion + "/" + t
		}

		var n = ["angular.js", "angular-animate.min.js", "angular-route.min.js", "angular-aria.min.js", "angular-messages.min.js"];
		return {all: t}
	}

	angular.module("docsApp").factory("$demoAngularScripts", ["BUILDCONFIG", e])
}(), angular.module("docsApp").constant("SERVICES", [{
	name: "$mdMedia",
	type: "service",
	outputPath: "partials/api/material.core/service/$mdMedia.html",
	url: "api/service/$mdMedia",
	label: "$mdMedia",
	module: "material.core",
	githubUrl: "https://github.com/angular/material/blob/master/src/core/util/media.js",
	hasDemo: !1
}]);
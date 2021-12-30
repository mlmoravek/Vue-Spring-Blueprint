import Vue from "vue";
import VueRouter, { RouteConfig } from "vue-router";
import Home from "../views/HomeView.vue";
import AlertService from "@/services/libs/AlertService";
import store from "@/store";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/register",
    name: "Register",
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    component: () =>
      import(/* webpackChunkName: "register" */ "../views/RegisterView.vue"),
  },
  {
    path: "/profile",
    name: "Profile",
    meta: {
      requiresAuth: true,
    },
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    component: () =>
      import(/* webpackChunkName: "profile" */ "../views/UserProfileView.vue"),
  },

  {
    path: "/accounts",
    name: "Accounts",
    meta: {
      requiresAuth: true,
    },
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    component: () =>
      import(/* webpackChunkName: "accounts" */ "../views/AccountsView.vue"),
  },
  {
    path: "/account/:id",
    name: "Account",
    props: true,
    meta: {
      requiresAuth: true,
    },
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    component: () =>
      import(
        /* webpackChunkName: "account" */ "../views/AccountDetailView.vue"
      ),
  },
  // and finally the default route, when none of the above matches:
  {
    path: "*",
    name: "404",
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    component: () =>
      import(/* webpackChunkName: "404" */ "../views/PageNotFound.vue"),
  },
];

const router = new VueRouter({
  mode: "history",
  //base: process.env.BASE_URL,
  base: process.env.VUE_APP_PUBLIC_PATH,
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    // this route requires auth, check if logged in
    // if not, redirect to Home page.
    if (!store.getters.isLoggedIn) {
      setLastRoute(from.name || "");
      AlertService.info("Authentication is required, redirected to Home");
      goToHome(next);
      return;
    }
  }
  // does not require auth
  // go to wherever I'm going
  next(); // make sure to always call next()!
});

let lastRoute = "";
/**
 * Change route to Home page
 */
function goToHome(next: Function | undefined = undefined): void {
  if (router.currentRoute.name != "Home") {
    console.debug("redirect to Home");
    const route = { name: "Home" };
    if (next) next(route);
    else router.push(route);
  }
}

/**
 * changes route to last route befor login or to home page
 */
function goToLastRoute(): void {
  console.debug("redirect to last route");
  // redirect to last route if set
  if (lastRoute) router.push({ path: lastRoute });
  // else redirect to home
  else goToHome();
}

function setLastRoute(route: string): void {
  lastRoute = route;
}

export { goToHome, goToLastRoute, setLastRoute };
export default router;

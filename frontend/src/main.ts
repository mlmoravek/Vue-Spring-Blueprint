import Vue, { CreateElement, VNode } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";

/** Import project plugins  */
// import Fontawesome from "@/plugins/fontawesome";
import Buefy from "@/plugins/buefy";
import Filter from "@/plugins/filter";

// Vue.use(Fontawesome);
Vue.use(Buefy);
// Vue.use(Buefy, { icon: "fontawesome" });
Vue.use(Filter);

/** import custom css style **/
import "@/assets/styles/style.scss";

Vue.config.productionTip = false;

function checkAuth(): Promise<void> {
  // Check if token is valid, refresh auth token and load user object
  return store.dispatch("preLoadUser");
}

function createApp(): void {
  new Vue({
    router,
    store,
    render: (h: CreateElement): VNode => h(App),
  }).$mount("#app");
}

// check auth before vue initialization
checkAuth().then(createApp);

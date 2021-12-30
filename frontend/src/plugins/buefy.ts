/**
 * This plugin imports the buefy component framework
 */
import _Vue from "vue";
import Buefy from "buefy";

// add Material Design Icons
import "@mdi/font/css/materialdesignicons.css"; // Disable if Fontawesome icons are used

// This is your plugin object. It can be exported to be used anywhere.
export default {
  // The install method is all that needs to exist on the plugin object.
  // It takes the global Vue object as well as user-defined options.
  install: (
    Vue: typeof _Vue,
    options: { icon: "fontawesome" | "mdi" } = { icon: "mdi" },
  ): void => {
    let buefy_options = {};

    // default library is Material Design Icons
    if (options.icon == "fontawesome")
      // add fontawsome config to buefy
      buefy_options = Object.assign(buefy_options, {
        // set fontawsome as default icon
        defaultIconComponent: "vue-fontawesome",
        defaultIconPack: "fa",
      });

    // add buefy to vue instance
    Vue.use(Buefy, buefy_options);

    console.log("Buefy initialized");
  },
};

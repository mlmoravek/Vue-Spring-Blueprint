/**
 * In this file globale filter could be defined.
 */

import _Vue from "vue";

// This is a plugin object. It can be exported to be used anywhere.
export default {
  // The install method is all that needs to exist on the plugin object.
  // It takes the global Vue object as well as user-defined options.
  install: (Vue: typeof _Vue, options?: any): void => {
    /** String to uppercase */
    Vue.filter("uppercase", (str: string) => str?.toUpperCase());
    /** String to lowercase */
    Vue.filter("lowercase", (str: string) => str?.toLocaleLowerCase());
    /** Sets a default if string is empty */
    Vue.filter("default", (str: string) => str || "/");
    /** Join all elements in a array */
    Vue.filter("arry_join", (array: Array<string>) => array?.join() || "");
    /** Convert a iso date string into german date format */
    Vue.filter("convert_german_date", (str: string, datetime = true) => {
      if (!str) return "-";
      const date = new Date(str);
      if (date == null) return str;

      const nDay = date.getDate();
      const day = nDay < 10 ? "0" + nDay : nDay;
      const nMonth = date.getMonth() + 1;
      const month = nMonth < 10 ? "0" + nMonth : nMonth;
      const hour = date.getHours();
      const min = date.getMinutes();

      return datetime
        ? `${day}.${month}.${date.getFullYear()} ${hour}:${min}`
        : `${day}.${month}.${date.getFullYear()}`;
    });
  },
  //....
};

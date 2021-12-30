/**
 * This Service helpes to set and get Variables from the LocalStorage
 */

export default class StorageService {
  /**
   * Set a variable to the localstorage.
   * JSON.stringify the variable.
   * @param key reference key
   * @param val variable
   */
  public static set(key: string, val: any): void {
    const json = JSON.stringify(val);
    localStorage.setItem(key, json);
  }

  /**
   * Get a variable from the localstorage.
   * Json.parse the variable.
   * Return undefined if the variable is not set or could be parsed.
   * @param key reference key
   * @returns Object or Array
   */
  public static get(key: string): any {
    const json = localStorage.getItem(key);
    if (json == "undefined") return undefined;
    try {
      return json ? JSON.parse(json) : undefined;
    } catch (err) {
      console.debug("localStorage error at key: " + key, err);
      this.set(key, "");
      return undefined;
    }
  }
}

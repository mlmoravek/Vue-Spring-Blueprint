/**
 * In this service file util functions could be defined.
 */

/**
 * Check if the enviroment mode is production
 * @returns boolean
 */
export const isProd = (): boolean => process.env.NODE_ENV === "production";

/**
 * This services wrapps axios. It provides functions for axios instance or static use.
 */

/* import axios */
import axios, {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  AxiosError,
} from "axios";
/* get production variable */
const isProd = process.env.NODE_ENV === "production";

type ErrorHandler = (error: AxiosError) => void;
type SuccessHandler = (response: AxiosResponse<any>) => any;

/**
 * Provides a service to make http requests
 */
export default class RequestService {
  private http: AxiosInstance;
  private baseURL: string;
  private errorHandler!: ErrorHandler;
  private successHandler!: SuccessHandler;
  private static instance: RequestService;

  /**
   * Create a RequestService with an AxiosInstance
   * @param baseURL  Set `baseURL` for an instance of axios to pass relative URLs to methods of that instance.
   * @param timeout `timeout` specifies the number of milliseconds before the request times out.
   * @param headers `headers` are custom headers to be sent
   */
  constructor(baseURL: string, timeout = 0, headers: object = {}) {
    this.http = axios.create({
      headers: Object.assign({ "Content-Type": "application/json" }, headers),
      baseURL,
      timeout,
    });
    this.baseURL = baseURL;
    RequestService.instance = this;
    this.errorHandler = RequestService.defaultErrorHandler;
    this.successHandler = RequestService.defaultSuccessHandler;
  }

  /**
   * Get the baseURL from the instance
   */
  public getBaseURL(): string {
    return this.baseURL;
  }

  /**
   * Set a custom error handler for all requests
   * @param f error function
   */
  public setErrorHandler(f: ErrorHandler): void {
    this.errorHandler = f;
  }

  /**
   * Set a custon success handler for all requests
   * @param f success fucntion
   */
  public setSuccessHandler(f: SuccessHandler): void {
    this.successHandler = f;
  }

  /**
   * Updated the default headers for the axios instance
   * @param headers header object
   */
  public addHeaders(headers: object): void {
    this.http.defaults.headers = Object.assign(
      this.http.defaults.headers,
      headers,
    );
  }

  /**
   * Makes a get request on the axios instance.
   * Uses setted handler functions if available.
   * @param url `url` is the server URL that will be used for the request
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers  `headers` are custom headers to be sent
   */
  public get<T>(
    url: string,
    params: object = {},
    headers: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = { headers, params };
    return this.http
      .get(url, config)
      .then((response: AxiosResponse<T>) =>
        this.successHandler ? this.successHandler(response) : response.data,
      )
      .catch((error) => (this.errorHandler ? this.errorHandler(error) : error));
  }

  /**
   * Makes a post request on the axios instance.
   * Uses setted handler functions if available.
   * @param url `url` is the server URL that will be used for the request
   * @param body `body` is the data to be sent as the request in the body
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers  `headers` are custom headers to be sent
   */
  public post<T>(
    url: string,
    body: object = {},
    params: object = {},
    headers: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      headers,
      params,
    };
    return this.http
      .post(url, body, config)
      .then((response: AxiosResponse<T>) =>
        this.successHandler ? this.successHandler(response) : response.data,
      )
      .catch((error) => (this.errorHandler ? this.errorHandler(error) : error));
  }

  /**
   * Makes a post request on the axios instance to upload a file.
   * Uses setted handler functions if available.
   * @param url `url` is the server URL that will be used for the request
   * @param body `body` is the data to be sent as the request in the body
   * @param params  `params` are the URL parameters to be sent with the request
   */
  public upload<T>(
    url: string,
    data: FormData,
    onUploadProgress: (e: any) => void,
    params: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      onUploadProgress,
      params,
    };
    return this.http
      .post(url, data, config)
      .then((response: AxiosResponse<T>) =>
        this.successHandler ? this.successHandler(response) : response.data,
      )
      .catch((error) => (this.errorHandler ? this.errorHandler(error) : error));
  }

  /**
   * Makes a get request on the axios instance todownload a file.
   * Uses setted handler functions if available.
   * @param url `url` is the server URL that will be used for the request
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers  `headers` are custom headers to be sent
   */
  public download<T>(
    url: string,
    params: object = {},
    headers: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      headers,
      params,
      responseType: "blob",
    };
    return this.http
      .get(url, config)
      .then((response: AxiosResponse<T>) =>
        this.successHandler ? this.successHandler(response) : response.data,
      )
      .catch((error) => (this.errorHandler ? this.errorHandler(error) : error));
  }

  /**
   * Makes a put request on the axios instance.
   * Uses setted handler functions if available.
   * @param url `url` is the server URL that will be used for the request
   * @param body `body` is the data to be sent as the request in the body
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers  `headers` are custom headers to be sent
   */
  public put<T>(
    url: string,
    body: object = {},
    params: object = {},
    headers: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      headers,
      params,
    };
    return this.http
      .put(url, body, config)
      .then((response: AxiosResponse<T>) =>
        this.successHandler ? this.successHandler(response) : response.data,
      )
      .catch((error) => (this.errorHandler ? this.errorHandler(error) : error));
  }

  /**
   * Makes a delete request on the axios instance.
   * Uses setted handler functions if available.
   * @param url `url` is the server URL that will be used for the request
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers  `headers` are custom headers to be sent
   * @param body `body` is the data to be sent as the request in the body
   */
  public delete<T>(
    url: string,
    params: object = {},
    headers: object = {},
    body: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      params,
      headers,
      data: body,
    };
    return this.http
      .delete(url, config)
      .then((response: AxiosResponse<T>) =>
        this.successHandler ? this.successHandler(response) : response.data,
      )
      .catch((error) => (this.errorHandler ? this.errorHandler(error) : error));
  }

  /** ---- Static Methods ---- */

  /**
   * Get the createt RequestService instance
   */
  public static getInstance(): RequestService {
    return RequestService.instance;
  }

  /**
   * Makes multiple get requests and wait till all finished.
   * @param urls Request url array
   * @param params Possible parameter for all urls
   */
  public static getAll(urls: string[], params: object = {}): Promise<any> {
    const requests = urls.map((url) => axios.get(url, params));
    return Promise.all(requests)
      .then((responses: AxiosResponse<any>[]) =>
        responses.map((response: AxiosResponse<any>) =>
          this.defaultSuccessHandler(response),
        ),
      )
      .catch((error) => this.defaultErrorHandler(error));
  }

  /**
   * Makes an independent get request.
   * @param url `url` is the server URL that will be used for the request
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers `headers` are custom headers to be sent
   */
  public static get<T>(
    url: string,
    params: object = {},
    headers: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      params,
      headers,
    };
    return axios
      .get(url, config)
      .then(this.defaultSuccessHandler)
      .catch(this.defaultErrorHandler);
  }

  /**
   * Makes an independent post request.
   * @param url `url` is the server URL that will be used for the request
   * @param body `body` is the data to be sent as the request in the body
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers `headers` are custom headers to be sent
   */
  public static post<T>(
    url: string,
    body: object = {},
    params: object = {},
    headers: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      params,
      headers,
    };
    return axios
      .post(url, body, config)
      .then(this.defaultSuccessHandler)
      .catch(this.defaultErrorHandler);
  }

  /**
   * Makes an independent put request.
   * @param url `url` is the server URL that will be used for the request
   * @param body `body` is the data to be sent as the request in the body
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers `headers` are custom headers to be sent
   */
  public static put<T>(
    url: string,
    body: object = {},
    params: object = {},
    headers: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      params,
      headers,
    };
    return axios
      .put(url, body, config)
      .then(this.defaultSuccessHandler)
      .catch(this.defaultErrorHandler);
  }

  /**
   * Makes an independent delete request.
   * @param url `url` is the server URL that will be used for the request
   * @param params  `params` are the URL parameters to be sent with the request
   * @param headers `headers` are custom headers to be sent
   * @param body `body` is the data to be sent as the request in the body
   */
  public static delete<T>(
    url: string,
    params: object = {},
    headers: object = {},
    body: object = {},
  ): Promise<T> {
    const config: AxiosRequestConfig = {
      params,
      headers,
      data: body,
    };
    return axios
      .delete(url, config)
      .then(this.defaultSuccessHandler)
      .catch(this.defaultErrorHandler);
  }

  public static defaultSuccessHandler<T>(response: AxiosResponse<T>): T {
    if (!isProd)
      console.info(
        response.status,
        response.request.responseURL,
        response.data,
      );
    else
      console.debug(
        response.status,
        response.request.responseURL,
        response.data,
      );
    return response.data;
  }

  public static defaultErrorHandler(error: AxiosError): void {
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      if (!isProd)
        console.warn(error.response.status, error.response.request.responseURL);
      else
        console.debug(
          error.response.status,
          error.response.request.responseURL,
        );
    } else if (error.request) {
      // The request was made but no response was received
      // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
      // http.ClientRequest in node.js
      console.error(error.request);
    } else {
      // Something happened in setting up the request that triggered an Error
      console.error(error.message);
    }
    throw error;
  }
}

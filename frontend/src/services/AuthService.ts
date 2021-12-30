import RequestService from "@/services/libs/RequestService";
import StorageService from "@/services/libs/StorageService";
import { AxiosError, AxiosResponse } from "axios";
import { isProd } from "./libs/Utils";

/**
 * Define Model type
 */
export interface AuthResponse {
  status: number;
  timestamp: number;
  // success fields
  id?: number;
  token: string;
  username?: string;

  // error fields
  error?: string;
  errors?: string[];
  message?: string;
  path?: string;
}

class AuthService<T extends AuthResponse = AuthResponse> {
  private requestService: RequestService;
  private unautorizedHandler?: Function;
  private clearSession = false;
  private static readonly TOKEN_STORAGE_KEY = "auth-token";
  private static readonly AUTH_STORAGE_KEY = "auth-data";
  private static readonly RESPONSE_TOKEN_HEADER = "X-Authorization";

  constructor(requestService: RequestService | undefined = undefined) {
    if (!requestService) {
      // initialize RequestService if not initialized
      const baseURL = process.env.VUE_APP_BASE_URL;
      // add Access-Control-Allow-Origin if is dev
      const header = !isProd ? { "Access-Control-Allow-Origin": "*" } : {};
      requestService = new RequestService(baseURL, undefined, header);
    }

    // set RequestService
    this.requestService = requestService;
    // set custom error handler
    this.requestService.setErrorHandler((e) => this.errorHandler(e));
    // set custom success handler
    this.requestService.setSuccessHandler((r) => this.successHandler(r));
    // load token from last session
    this.setToken(StorageService.get(AuthService.TOKEN_STORAGE_KEY));

    console.log("AuthService initialized");
  }

  public getAuthenticatedRequestService(): RequestService {
    return this.requestService;
  }

  public setUnauthorizedHandler(f: Function): void {
    this.unautorizedHandler = f;
    console.debug("Register unauthorized handler", f);
  }

  public clearSessionOnUnauthorizedException(b: boolean): void {
    this.clearSession = b;
  }

  public static isLoggedIn(): boolean {
    return AuthService.getToken() ? true : false;
  }

  public static getAuthData(): AuthResponse | undefined {
    return StorageService.get(AuthService.AUTH_STORAGE_KEY);
  }

  public static getToken(): string | undefined {
    return StorageService.get(AuthService.TOKEN_STORAGE_KEY);
  }
  /**
   * log in and sets user for further requests
   * @param username user name
   * @param password user password
   * @returns Promise
   */
  public login(username: string, password: string): Promise<T> {
    const params = { username, password };

    // do login request
    return this.requestService
      .post<T>("/login", undefined, params)
      .then((res: T) => {
        if (res.status === 200) {
          // set user
          this.setToken(res.token);
          // set user to storage
          StorageService.set(AuthService.AUTH_STORAGE_KEY, res);
          return res;
        } else {
          console.error(res);
          throw res.error || "Anmeldung Fehlgeschlagen";
        }
      });
  }

  /**
   * Refresh the auth token for further requests.
   * Also check if token is valid
   * @returns Promise<boolean>
   */
  public refreshToken(): Promise<boolean> {
    // be sure to clear session if token is invalid
    const clearSessionBefore = this.clearSession;
    this.clearSession = true;
    // do refresh request to check if token is valid
    // also token will be refreshed on success handler
    return new Promise((res, rej) =>
      // check if token is given else throw error before request
      this.checkAuthentication()
        .then(() =>
          this.requestService
            .get<void>("/refresh")
            .then(() => res(true))
            .catch(() => rej(false))
            // restore clearSession behavior after request
            .finally(() => (this.clearSession = clearSessionBefore)),
        )
        .catch((e) => rej(e)),
    );
  }

  /**
   * clear user
   */
  public logout(): void {
    // remove user token
    this.setToken();
  }

  /**
   * checks if the user is logged in, if not it throws an error
   */
  public checkAuthentication(): Promise<boolean> {
    return new Promise((res, rej) => {
      if (!AuthService.isLoggedIn()) {
        // throw not authorised error
        rej("Not authenticated");
      }
      res(true);
    });
  }

  /**
   * set or clear user and authorization header for requests
   * @param token token
   */
  protected setToken(token: string | undefined = undefined): void {
    // add Bearer prefix if not given
    if (token && !token?.startsWith("Bearer ")) token = "Bearer " + token;
    // update authorization header for requests
    this.requestService.addHeaders({ authorization: token });
    // update user in storage
    StorageService.set(AuthService.TOKEN_STORAGE_KEY, token);
  }

  /**
   * set a new token for a successful request
   * @param response AxiosResponse
   * @returns T
   */
  private successHandler<T>(response: AxiosResponse<T>): T {
    const refreshToken =
      response.headers[AuthService.RESPONSE_TOKEN_HEADER.toLowerCase()];
    // 'this' is not the correct context - use the instance instead
    if (refreshToken) authServiceInstance.setToken(refreshToken);
    return RequestService.defaultSuccessHandler(response);
  }

  /**
   * Validate the repsonse for different status codes
   * @param error AxiosError
   * @returns response data or throws error
   */
  private errorHandler(error: AxiosError): any {
    let errorMessage;

    if (error.response?.data) {
      const data: AuthResponse = error.response.data;

      if (data.errors?.length) errorMessage = data.errors[0];
      else if (data.error) errorMessage = data.error;
      else if (typeof error.response.data === "string")
        errorMessage = error.response.data;
    }

    switch (error.response?.status) {
      case 400: // wrong input
        if (!errorMessage) errorMessage = "Fehlerhafte Eingabe";
        console.error(errorMessage, error);
        break;

      case 401:
      case 403:
        // unauthorized
        if (!errorMessage) errorMessage = "Unautorized";
        // show error message
        // AlertService.error("Autorisierungs Fehler");
        console.error(errorMessage, error);

        if (this.clearSession)
          // user is not valid anymore -> delete token
          this.setToken();

        if (this.unautorizedHandler)
          this.unautorizedHandler(errorMessage, error);
        break;

      default:
        // unexpected response
        errorMessage = "Verbinungsprobleme zum Server";
        console.error(errorMessage, error);
        // show error message
        // AlertService.error("Verbinungsprobleme zum Server");
        break;
    }

    // throw error message
    throw errorMessage;
  }
}

const authServiceInstance = new AuthService();

export { authServiceInstance };
export default AuthService;

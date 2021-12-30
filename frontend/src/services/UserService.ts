import { NewUser, User } from "@/types";
import AuthService, { authServiceInstance } from "./AuthService";
import { accountServiceInstance } from "./AccountService";

import RequestService from "./libs/RequestService";

class UserService {
  private readonly authService: AuthService;
  private readonly requestService: RequestService;

  constructor() {
    // initialize AuthService
    this.authService = authServiceInstance;
    // get RequestService with auth header
    this.requestService = authServiceInstance.getAuthenticatedRequestService();
  }

  public getUser(): Promise<User> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() => this.requestService.get<User>("/user"));
  }

  public changePassword(
    oldPassword: string,
    newPassword: string,
  ): Promise<void> {
    // check if user is authenticated before request
    return this.authService.checkAuthentication().then(() =>
      this.requestService.post<void>("/user/update/password", {
        oldPassword,
        newPassword,
      }),
    );
  }

  public changeUsername(username: string): Promise<void> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() =>
        this.requestService.post<void>("/user/update/username", { username }),
      );
  }

  public checkUsernameExists(username: String): Promise<boolean> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() =>
        this.requestService.get<boolean>("/user/check", { username }),
      );
  }

  public update(user: User): Promise<User> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() => accountServiceInstance.update(user));
  }

  public register(user: NewUser): Promise<User> {
    // public endpoint no need to check authentication before
    return this.requestService.put<User>("/registration", user);
  }
}

const userServiceInstance = new UserService();

export { userServiceInstance };
export default UserService;

import { Page, User } from "@/types";
import AuthService, { authServiceInstance } from "./AuthService";
import RequestService from "./libs/RequestService";

class AccountService {
  private readonly authService: AuthService;
  private readonly requestService: RequestService;

  constructor() {
    // initialize AuthService
    this.authService = authServiceInstance;
    // get RequestService with auth header
    this.requestService = authServiceInstance.getAuthenticatedRequestService();
  }

  public get(): Promise<User[]> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() => this.requestService.get<User[]>("/accounts"));
  }

  public getById(id: string | number): Promise<User> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() => this.requestService.get<User>("/accounts/" + id));
  }

  public update(user: User): Promise<User> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() =>
        this.requestService.post<User>(
          "/accounts/" + user.id + "/update",
          user,
        ),
      );
  }

  public changePassword(
    id: string | number,
    newPassword: string,
  ): Promise<User> {
    // check if user is authenticated before request
    return this.authService.checkAuthentication().then(() =>
      this.requestService.post<User>("/accounts/" + id + "/password", {
        newPassword,
      }),
    );
  }

  public delete(id: String | number): Promise<void> {
    // check if user is authenticated before request
    return this.authService
      .checkAuthentication()
      .then(() => this.requestService.delete<void>("/accounts/" + id));
  }

  public getPage(
    page: number,
    size?: number,
    sort?: string,
    ascending?: boolean,
    search?: string,
    filter?: string,
  ): Promise<Page<User>> {
    const params = {
      page,
      size,
      sort,
      ascending,
      search,
      filter,
    };

    return this.authService
      .checkAuthentication()
      .then(() =>
        this.requestService.get<Page<User>>("/accounts/page", params),
      );
  }

  // DEBUG
  public addTestAccounts(): Promise<void> {
    return this.authService
      .checkAuthentication()
      .then(() => this.requestService.get<void>("/accounts/create_test"));
  }
}

const accountServiceInstance = new AccountService();

export { accountServiceInstance };
export default AccountService;

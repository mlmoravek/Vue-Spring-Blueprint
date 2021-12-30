import Vue from "vue";
import Vuex, { StoreOptions } from "vuex";
import { User } from "@/types";
import { userServiceInstance as userService } from "@/services/UserService";
import AlertService from "@/services/libs/AlertService";
import { authServiceInstance as authService } from "@/services/AuthService";

Vue.use(Vuex);

export interface RootState {
  user?: User;
}

const storeOptions: StoreOptions<RootState> = {
  state: {
    user: undefined,
  },
  getters: {
    isLoggedIn(state): boolean {
      return state.user !== undefined;
    },
  },
  mutations: {
    setUser(state, user: User): void {
      state.user = user;
    },
    clearUser(state): void {
      state.user = undefined;
    },
  },
  actions: {
    /**
     * Check if token is valid,
     * refresh auth token
     * and load user object
     */
    preLoadUser({ dispatch }): Promise<void> {
      return authService
        .refreshToken()
        .then(() => dispatch("loadUser"))
        .then((user: User) =>
          console.log("Preload user", "[" + user.id + "]", user.username),
        )
        .catch(() => console.debug("No user pre logged in"));
    },
    /** load current user from server */
    loadUser({ state }): Promise<User | undefined> {
      return userService
        .getUser()
        .then((user: User) => (state.user = user))
        .catch((err) => {
          AlertService.error(err);
          return undefined;
        });
    },
  },
  modules: {},
};

const store = new Vuex.Store<RootState>(storeOptions);
export default store;

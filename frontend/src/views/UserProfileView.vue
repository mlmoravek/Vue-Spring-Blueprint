<template>
  <div class="container">
    <form class="section" @submit="onProfileSave">
      <h1 class="title">Profile</h1>
      <b-notification
        v-show="updateProfileError"
        type="is-danger"
        aria-close-label="Close notification"
        :message="updateProfileError"
        @close="updateProfileError = ''"
      />
      <b-field grouped>
        <b-field label="Vorname" expanded>
          <b-input v-model="user.firstName" required />
        </b-field>
        <b-field label="Nachname" expanded>
          <b-input v-model="user.lastName" required />
        </b-field>
      </b-field>
      <b-field label="Email" expanded>
        <b-input type="email" v-model="user.email" required />
      </b-field>
      <b-field>
        <b-button
          label="Speichern"
          native-type="submit"
          aria-label="Speichern Button"
        />
      </b-field>
    </form>

    <form class="section" @submit="onUsernameChange">
      <h1 class="title">Benutzername Ändern</h1>
      <b-notification
        v-show="changeUsernameError"
        type="is-danger"
        aria-close-label="Close notification"
        :message="changeUsernameError"
        @close="changeUsernameError = ''"
      />
      <b-field label="Username" expanded>
        <b-input v-model="user.username" required />
      </b-field>
      <b-field>
        <b-button
          label="Username ändern"
          native-type="submit"
          aria-label="Username ändern Button"
        />
      </b-field>
    </form>

    <form class="section" @submit="onPasswordChange">
      <h1 class="title">Passwort Ändern</h1>
      <b-notification
        v-show="changePasswordError"
        type="is-danger"
        aria-close-label="Close notification"
        :message="changePasswordError"
        @close="changePasswordError = ''"
      />
      <b-field label="Aktuelles Passwort" expanded>
        <b-input
          v-model="password.oldPassword"
          type="password"
          password-reveal
          required
        />
      </b-field>

      <PasswordInput
        v-model="password.newPassword"
        @input="(e) => (passwordValidationError = e)"
        @confirm="(p) => (password.confirmPassword = p)"
      />

      <b-field>
        <b-button
          label="Password ändern"
          native-type="submit"
          aria-label="Passwort ändern Button"
        />
      </b-field>
    </form>
    <b-loading :is-full-page="true" v-model="isLoading" />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { User } from "@/types";
import { userServiceInstance as userService } from "@/services/UserService";
import { accountServiceInstance as accountService } from "@/services/AccountService";
import AlertService from "@/services/libs/AlertService";
import PasswordInput from "@/components/controls/PasswordInput.vue";

export default Vue.extend({
  name: "UserProfileView",
  components: {
    PasswordInput,
  },
  data: function () {
    return {
      user: {} as User,
      password: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      passwordValidationError: "",
      isLoading: false,
      updateProfileError: "",
      changeUsernameError: "",
      changePasswordError: "",
    };
  },
  mounted() {
    this.isLoading = true;
    const loggedInUser = this.$store.state.user as User;

    accountService
      .getById(loggedInUser.id)
      .then((user: User) => (this.user = user || loggedInUser))
      .catch((error: any) =>
        AlertService.error(
          "Aktuell besteht keine Verbindung!",
          "Bitte versuchen Sie es in einiger Zeit erneut!",
          "Wenn das Problem weiterhin besteht, informieren Sie bitte den Helpdesk!",
          "Fehlermeldung: " + error,
        ),
      )
      .finally(() => (this.isLoading = false));
  },
  methods: {
    onProfileSave(e: Event) {
      e.preventDefault();
      this.isLoading = true;
      this.updateProfileError = "";

      userService
        .update(this.user)
        .then((user: User) => {
          this.user = user;
          this.$store.commit("setUser", user);
          AlertService.success("Profil aktualisiert");
        })
        .catch((err: Error) => (this.updateProfileError = err.message))
        .finally(() => (this.isLoading = false));
    },
    onUsernameChange(e: Event) {
      e.preventDefault();
      if (!this.user.username) {
        this.changeUsernameError = "Ein Benutzername muss angegeben werden";
        return;
      }
      this.isLoading = true;
      this.changeUsernameError = "";

      const callUpdate = (): Promise<any> =>
        userService
          .changeUsername(this.user.username)
          .then(() => AlertService.success("Benutzername aktualisiert"))
          .catch((err: Error) => (this.changeUsernameError = err.message))
          .finally(() => (this.isLoading = false));

      if (this.user.username != this.$store.state.user.username) {
        // username has changed
        // check if new username already exist
        userService
          .checkUsernameExists(this.user.username)
          .then((exist: boolean) => {
            // error new username is already taken
            if (exist)
              this.changeUsernameError = "Benutzername existiert bereits";
            // else do update
            else callUpdate();
          })
          .catch((err: Error) => (this.changeUsernameError = err.message));
      }
      // if same username do update
      else callUpdate();
    },
    onPasswordChange(e: Event) {
      e.preventDefault();
      if (!this.password.oldPassword) {
        this.changePasswordError =
          "Das aktuelle Passwort muss angegeben werden";
        return;
      }
      if (!this.password.newPassword && !this.passwordValidationError) {
        this.changePasswordError = "Ein valides Passwort muss angegeben werden";
        return;
      }
      this.isLoading = true;
      this.changePasswordError = "";
      userService
        .changePassword(this.password.oldPassword, this.password.newPassword)
        .then(() => AlertService.success("Passwort aktualisiert"))
        .catch((err: Error) => (this.changePasswordError = err.message))
        .finally(() => (this.isLoading = false));
    },
  },
});
</script>

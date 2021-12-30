<template>
  <DialogWrapper
    v-model="value"
    title="Login"
    width="330"
    :error="error"
    @close="close"
  >
    <template v-slot:default>
      <form>
        <div class="content">
          <b-field class="control" label="Nutzername">
            <b-input
              v-model="username"
              type="text"
              custom-class="has-border-grey"
              placeholder="@Benutzername"
              expanded
              required
            />
          </b-field>
        </div>
        <div class="content">
          <b-field class="control" label="Password">
            <b-input
              v-model="password"
              type="password"
              custom-class="has-border-grey"
              placeholder="*******"
              password-reveal
              required
              expanded
              @keyup.enter.native="onLogin"
            />
          </b-field>
        </div>
      </form>
      <b-loading :is-full-page="true" v-model="isLoading" />
    </template>
    <template v-slot:footer>
      <b-button
        class="is-justify-content-center"
        type="is-success"
        expanded
        outlined
        @click="onLogin"
        aria-label="Login Button"
        label="Login"
      />
    </template>
  </DialogWrapper>
</template>

<script lang="ts">
import Vue from "vue";
import DialogWrapper from "@/components/dialog/DialogWrapper.vue";
import { authServiceInstance as authService } from "@/services/AuthService";
import AlertService from "@/services/libs/AlertService";
import { goToLastRoute } from "@/router";

export default Vue.extend({
  name: "LoginDialog",
  components: { DialogWrapper },
  props: {
    value: {
      type: Boolean,
      default: false,
    },
  },
  data: function () {
    return {
      username: "",
      password: "",
      error: "",
      isLoading: false,
    };
  },
  watch: {
    value() {
      this.checkIfAlreadyLoggedIn();
    },
  },
  mounted() {
    this.checkIfAlreadyLoggedIn();
  },
  methods: {
    onLogin() {
      this.error = "";
      if (!this.username || !this.password)
        this.error = "Bitte zuerst Benutzername und Password eingeben.";
      else {
        this.isLoading = true;
        authService
          .login(this.username, this.password)
          // load current user after login
          .then(() => this.$store.dispatch("loadUser"))
          // close dialog
          .then(() => {
            // show success message
            AlertService.success("Angemeldet");
            goToLastRoute();
            this.close();
          })
          // catch error
          .catch((err: Error) => (this.error = err.message || err.toString()))
          // close loading state
          .finally(() => (this.isLoading = false));
      }
    },
    checkIfAlreadyLoggedIn() {
      if (this.value && this.$store.getters.isLoggedIn) {
        AlertService.warning("Zuerst abmelden");
        this.close();
      }
    },
    close() {
      this.error = "";
      this.$emit("input", false);
    },
  },
});
</script>

<style lang="scss" scoped></style>

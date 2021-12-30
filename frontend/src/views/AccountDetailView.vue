<template>
  <div class="container">
    <form class="section" @submit="onSaveAccount">
      <h1 class="title">Account Details</h1>
      <b-notification
        v-show="changeAccountError"
        type="is-danger"
        aria-close-label="Close notification"
        :message="changeAccountError"
        @close="updateAccountError = ''"
      />
      <b-field label="Nutzername" expanded>
        <b-input v-model="user.username" required />
      </b-field>
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

      <footer class="is-flex is-justify-content-flex-end">
        <b-button
          type="is-danger"
          outlined
          @click="onDeleteAccount()"
          label="Entfernen"
          aria-label="Entfernen Button"
        />

        <b-button
          type="is-success"
          class="ml-2"
          outlined
          native-type="submit"
          label="Speichern"
          aria-label="Speichern Button"
        />
      </footer>
    </form>

    <form class="section" @submit="onChangePassword">
      <h1 class="title">Passwort Ändern</h1>
      <b-notification
        v-show="changePasswordError"
        type="is-danger"
        aria-close-label="Close notification"
        :message="changePasswordError"
        @close="changePasswordError = ''"
      />

      <PasswordInput
        v-model="newPassword"
        @input="(e) => (passwordValidationError = e)"
      />

      <b-field>
        <b-button
          label="Passwort ändern"
          native-type="submit"
          aria-label="Passwort ändern Button"
        />
      </b-field>
    </form>

    <DeleteDialog v-model="openDeleteDialog" @delete="onDeleteConfirm" />
    <SaveDialog v-model="openSaveDialog" @save="onSaveConfirm" />
    <DialogWrapper
      v-model="openPasswordChangeDialog"
      title="Passwort ändern"
      content="Das Passwort wirklich ändern?"
      width="640"
      saveLabel="Ändern"
      @save="onPasswordChangeConfirm"
    />
    <b-loading :is-full-page="true" v-model="isLoading" />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import DialogWrapper from "@/components/dialog/DialogWrapper.vue";
import DeleteDialog from "@/components/dialog/DeleteDialog.vue";
import SaveDialog from "@/components/dialog/SaveDialog.vue";
import { accountServiceInstance as accountService } from "@/services/AccountService";
import AlertService from "@/services/libs/AlertService";
import { User } from "@/types";
import PasswordInput from "@/components/controls/PasswordInput.vue";

export default Vue.extend({
  name: "AccountDetailView",
  components: {
    DialogWrapper,
    DeleteDialog,
    SaveDialog,
    PasswordInput,
  },
  props: {
    // this prop is injected by router
    id: {
      type: String,
      required: true,
    },
  },
  data: function () {
    return {
      isLoading: false,
      openDeleteDialog: false,
      openSaveDialog: false,
      openPasswordChangeDialog: false,
      user: {} as User,
      newPassword: "",
      changeAccountError: "",
      changePasswordError: "",
      passwordValidationError: "",
    };
  },
  mounted() {
    this.isLoading = true;
    accountService
      .getById(this.id)
      .then((user: User) => (this.user = user))
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
    onDeleteAccount() {
      this.openDeleteDialog = true;
    },
    onSaveAccount(e: Event) {
      e.preventDefault();
      if (!this.user.username) {
        this.changeAccountError = "Ein Benutername muss angegeben werden";
        return;
      }

      this.changeAccountError = "";
      this.openSaveDialog = true;
    },
    onChangePassword(e: Event) {
      e.preventDefault();
      if (!this.newPassword) {
        this.changePasswordError =
          "Das aktuelle Passwort muss angegeben werden";
        return;
      }
      if (!this.passwordValidationError) {
        this.changePasswordError = "Validierungs Fehler.";
        return;
      }

      this.changePasswordError = "";
      this.openPasswordChangeDialog = true;
    },
    onDeleteConfirm() {
      this.isLoading = true;
      accountService
        .delete(this.user.id)
        .then(() => {
          AlertService.info("Account gelöscht");
          // leave this route after deleting
          this.$router.back();
        })
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
    onSaveConfirm() {
      this.isLoading = true;
      accountService
        .update(this.user)
        .then((user: User) => {
          this.user = user;
          AlertService.info("Account aktualisiert");
        })
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
    onPasswordChangeConfirm() {
      this.isLoading = true;
      accountService
        .changePassword(this.user.id, this.newPassword)
        .then((user: User) => {
          this.user = user;
          AlertService.info("Passwort aktualisiert");
        })
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
  },
});
</script>

<style lang="scss" scoped></style>

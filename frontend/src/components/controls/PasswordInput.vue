<template>
  <div class="field">
    <b-field
      label="Neues Passwort"
      expanded
      :type="passwordStrengthError ? 'is-danger' : ''"
      :message="passwordStrengthError"
    >
      <b-input
        v-model="password"
        type="password"
        password-reveal
        required
        @input="checkPasswordStrength()"
      />
    </b-field>
    <b-field
      label="Passwort bestätigen"
      expanded
      :type="passwordValidationError ? 'is-danger' : ''"
      :message="passwordValidationError"
    >
      <b-input
        v-model="confirmPassword"
        type="password"
        password-reveal
        required
        @input="checkConfirmPassword()"
      />
    </b-field>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

/**
 * Emits @input contains password property
 * Emits @confirm contains confirm password property
 */
export default Vue.extend({
  name: "PasswordInput",
  props: {
    value: {
      type: String,
      required: true,
    },
  },
  data: function () {
    return {
      password: "",
      confirmPassword: "",
      passwordStrengthError: "",
      passwordValidationError: "",
    };
  },
  computed: {
    isValid(): boolean {
      return (
        this.passwordValidationError == "" && this.passwordStrengthError == ""
      );
    },
  },
  watch: {
    password(): void {
      this.$emit("input", this.password);
    },
    confirmPassword(): void {
      this.$emit("confirm", this.confirmPassword);
    },
  },
  mounted() {
    this.password = this.value;
  },
  methods: {
    checkPasswordStrength() {
      // check if password match regular password strength validation
      // see: https://www.ocpsoft.org/tutorials/regular-expressions/password-regular-expression/
      const regex = new RegExp(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})",
      );
      if (!regex.test(this.password))
        this.passwordStrengthError =
          "Das Password muss mindestens 8 Zeichen lang sein und mindestens einen groß, sowie einen klein Buchstaben, eine Zahl und ein Sonderzeichen beinhalten";
      else this.passwordStrengthError = "";
      // emit is valid or not
      this.$emit("valid", this.isValid);
    },
    checkConfirmPassword() {
      if (this.password != this.confirmPassword)
        this.passwordValidationError = "Password is not matching";
      else this.passwordValidationError = "";
      // emit is valid or not
      this.$emit("valid", this.isValid);
    },
  },
});
</script>

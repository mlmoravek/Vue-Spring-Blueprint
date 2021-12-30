<template>
  <div id="app">
    <Header />

    <Breadcrumb :list="list" class="is-hidden-mobile" />

    <router-view />

    <Footer />

    <ScollTopButton :mobileOnly="false" />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Header from "@/components/layout/Header.vue";
import Footer from "@/components/layout/Footer.vue";
import Breadcrumb from "@/components/layout/Breadcrumb.vue";
import ScollTopButton from "@/components/layout/ScollTopButton.vue";
import { accountServiceInstance } from "./services/AccountService";

export default Vue.extend({
  name: "App",
  components: {
    Header,
    Footer,
    ScollTopButton,
    Breadcrumb,
  },
  computed: {
    list(): any[] {
      return this.$route.matched;
    },
  },
  mounted() {
    accountServiceInstance
      .addTestAccounts()
      .catch(() => console.warn("could not load dummy data"));
  },
});
</script>

<style lang="scss" scoped></style>

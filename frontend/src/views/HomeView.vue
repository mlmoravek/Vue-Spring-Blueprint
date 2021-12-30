<template>
  <div>
    <section class="section has-text-centered">
      <p class="title has-text-primary">
        Das ist ein Vue-Spring Starterprojekt für neue Projekte.
      </p>
      <img alt="Vue logo" src="../assets/images/dog.jpg" />
    </section>
    <section class="hero is-medium is-info">
      <div class="hero-body">
        <p class="title">Log dich ein für mehr ;)</p>
        <p class="subtitle">Username: admin | Passwort: admin</p>
      </div>
    </section>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "HomeView",
  components: {},
});
</script>

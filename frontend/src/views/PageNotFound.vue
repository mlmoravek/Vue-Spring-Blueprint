<template>
  <section class="section has-text-centered">
    <img alt="Vue logo" class="image" src="../assets/images/404.jpg" />
    <p class="title has-text-primary m-6">
      <b-button
        tag="router-link"
        :to="{ name: 'Home' }"
        type="is-primary"
        size="is-large"
        style="width: 300px"
        aria-label="Home Button"
        label="Home"
      />
    </p>
  </section>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "PageNotFound",
  components: {},
});
</script>

<style lang="scss" scoped>
.image {
  max-height: 65vh;
  margin: auto;
}
</style>

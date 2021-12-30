<template>
  <b-button
    v-show="isBackToTopVisible"
    class="goTop"
    :class="{ 'is-hidden-desktop': mobileOnly }"
    rounded
    outlined
    type="is-primary"
    @click="scrollToTop"
  >
    <b-icon icon="chevron-up" size="is-large" />
  </b-button>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "ScollTopButton",
  props: {
    mobileOnly: {
      type: Boolean,
      default: false,
    },
  },
  data: function () {
    return {
      isBackToTopVisible: false,
    };
  },
  mounted() {
    window.addEventListener("scroll", this.scrollListener);
  },
  beforeDestroy() {
    window.removeEventListener("scroll", this.scrollListener);
  },
  methods: {
    scrollListener: function () {
      const supportPageOffset = window.pageXOffset !== undefined;
      const isCSS1Compat = (document.compatMode || "") === "CSS1Compat";

      const scrollTop = supportPageOffset
        ? window.pageYOffset
        : isCSS1Compat
        ? document.documentElement.scrollTop
        : document.body.scrollTop;

      this.isBackToTopVisible = scrollTop > 250 ? true : false;
    },
    scrollToTop() {
      window.scrollTo(0, 0);
    },
  },
});
</script>

<style lang="scss" scoped>
.goTop {
  border-radius: 5px;
  position: fixed;
  right: 15px;
  bottom: 15px;
  width: 45px;
  height: 45px;
  display: block;
  &:hover,
  &:focus {
    opacity: 1;
    color: white !important;
  }

  .icon {
    margin-top: 1px;
    margin-left: calc(-0.5em - 2px) !important;
  }
  opacity: 0.8;
  z-index: 4;
  transition: all 0.4s ease-in;
}
</style>

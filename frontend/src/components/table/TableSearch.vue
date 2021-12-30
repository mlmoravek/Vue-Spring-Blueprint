<template>
  <div class="search-container field has-addons">
    <div class="control has-icons-right">
      <input
        placeholder="Suchen..."
        class="input"
        type="text"
        name="search"
        :value="input"
        v-on:keyup.enter="emitClick()"
        @input="emitChange($event.target.value)"
      />
      <span
        v-show="input"
        class="icon is-right is-clickable"
        @click="timeClick()"
      >
        <b-icon icon="times" />
      </span>
    </div>
    <div class="control">
      <b-button
        class="button is-primary"
        type="button"
        icon-right="magnify"
        aria-label="Suche starten"
        @click="emitClick()"
      />
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "TableSearch",
  data: function () {
    return {
      input: "",
    };
  },
  methods: {
    emitChange(text: string) {
      this.input = text;
      this.$emit("change", this.input);
    },
    emitClick() {
      this.$emit("search", this.input);
    },
    timeClick() {
      this.input = "";
      this.emitClick();
    },
  },
});
</script>

<style scoped lang="scss">
.search-container {
  max-width: 300px;
  background-color: white;
}

@media (max-width: 768px) {
  .search-container {
    max-width: 100%;
    width: 100%;
    > .control:first-child {
      width: 100%;
    }
  }
}
</style>

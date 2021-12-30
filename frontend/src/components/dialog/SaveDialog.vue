<template>
  <DialogWrapper
    v-model="open"
    title="Speichern"
    content="Dieses Objekt wirklich speichern?"
    width="640"
    :showDelete="false"
    :showSave="true"
    @save="onSave()"
  />
</template>

<script lang="ts">
import Vue from "vue";
import DialogWrapper from "@/components/dialog/DialogWrapper.vue";

export default Vue.extend({
  name: "SaveDialog",
  components: { DialogWrapper },
  props: {
    value: {
      type: Boolean,
      required: true,
    },
  },
  data: function () {
    return { open: this.value };
  },
  watch: {
    value() {
      this.open = this.value;
    },
    open() {
      this.$emit("input", this.open);
    },
  },
  methods: {
    onSave() {
      this.$emit("save");
    },
  },
});
</script>

<style lang="scss" scoped></style>

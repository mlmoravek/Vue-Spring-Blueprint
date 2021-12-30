<template>
  <DialogWrapper
    v-model="open"
    title="Löschen"
    content="Wirklich dieses Objekt entfernen?"
    width="640"
    :showDelete="true"
    :showSave="false"
    @delete="onDelete()"
  />
</template>

<script lang="ts">
import Vue from "vue";
import DialogWrapper from "@/components/dialog/DialogWrapper.vue";

export default Vue.extend({
  name: "DeleteDialog",
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
    onDelete() {
      this.$emit("delete");
    },
  },
});
</script>

<style lang="scss" scoped></style>

<template>
  <b-modal
    v-model="value"
    v-bind="$attrs"
    trap-focus
    :destroy-on-hide="false"
    aria-role="dialog"
    :aria-label="title + 'Modal'"
    aria-modal
    has-modal-card
    @close="onCancel()"
  >
    <article class="modal-card is-primary" style="width: auto">
      <header class="modal-card-head has-background-primary">
        <slot name="header" v-if="hasSlot('header') || title">
          <p class="modal-card-title">{{ title }}</p>
          <button type="button" class="delete" @click="onCancel()" />
        </slot>
      </header>
      <section class="modal-card-body">
        <div class="content" v-if="error">
          <b-notification
            type="is-danger"
            aria-close-label="Close notification"
            :message="error"
            @close="error = ''"
          />
        </div>
        <slot v-if="hasSlot('default') || content">
          <div class="content">
            <h4 class="title">{{ content }}</h4>
          </div>
        </slot>
      </section>
      <footer class="modal-card-foot is-block">
        <slot name="footer">
          <div class="level">
            <div class="level-left">
              <b-button
                v-if="showDelete"
                type="is-danger"
                class="mr-1"
                @click="onDelete()"
                :label="deleteLabel"
                :aria-label="deleteLabel + ' Button'"
              />
            </div>
            <div class="level-right">
              <b-button
                v-if="showCancel"
                class="ml-1"
                ref="cancelButton"
                @click="onCancel()"
                :label="cancelLabel"
                :aria-label="cancelLabel + ' Button'"
              />
              <b-button
                v-if="showSave"
                type="is-success"
                class="ml-1"
                @click="onSave()"
                :label="saveLabel"
                :aria-label="saveLabel + ' Button'"
              />
            </div>
          </div>
        </slot>
      </footer>
    </article>
  </b-modal>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "DialogWrapper",
  props: {
    /** open state of the dialog */
    value: {
      type: Boolean,
      default: false,
    },
    /** title of the dialog */
    title: {
      type: String,
    },
    /** shows a error message */
    error: {
      type: String,
    },
    /** if delete button is shown */
    showDelete: {
      type: Boolean,
      default: false,
    },
    /** if save button is shown */
    showSave: {
      type: Boolean,
      default: true,
    },
    /** if cancel button is shown */
    showCancel: {
      type: Boolean,
      default: true,
    },
    /** define the content text in the content slot */
    content: {
      type: String,
      default: "",
    },
    /** define the save button label */
    saveLabel: {
      type: String,
      default: "Speichern",
    },
    /** define the delete button label */
    deleteLabel: {
      type: String,
      default: "Löschen",
    },
    /** define the cancel button label */
    cancelLabel: {
      type: String,
      default: "Abbrechen",
    },
  },
  methods: {
    onCancel() {
      this.$emit("input", false);
      this.$emit("cancel");
    },

    onDelete() {
      this.$emit("delete");
      this.onCancel();
    },

    onSave() {
      this.$emit("save");
      this.onCancel();
    },
    /** check if slot exists */
    hasSlot(slot: string) {
      return !!this.$slots[slot];
    },
  },
});
</script>

<style lang="scss" scoped></style>

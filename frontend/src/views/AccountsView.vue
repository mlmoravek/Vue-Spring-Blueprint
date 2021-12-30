<template>
  <div class="container">
    <section class="section">
      <h1 class="title">Accounts</h1>
      <PaginatedTable
        :header="tableHeader"
        :options="tableOptions"
        :checkable="false"
        :row-class="() => 'is-clickable'"
        :load="loadPage"
        @click="onClick"
      >
        <template v-slot:func>
          <b-button
            @click="newAccount()"
            label="Hinzufügen"
            aria-label="Hinzufügen Button"
          />
        </template>
      </PaginatedTable>
    </section>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import PaginatedTable, { Action, Options, Header } from "@/components/table";
import { accountServiceInstance as accountService } from "@/services/AccountService";
import { Page, User } from "@/types";
import AlertService from "@/services/libs/AlertService";

export default Vue.extend({
  name: "AccountsView",
  components: {
    PaginatedTable,
  },
  data: function () {
    return {
      tableHeader: [
        {
          label: "ID",
          field: "id",
          numeric: true,
          sortable: true,
          width: "20px",
        },
        {
          label: "Username",
          field: "username",
          sortable: true,
        },
        {
          label: "Vorname",
          field: "firstName",
          sortable: true,
        },
        {
          label: "Nachname",
          field: "lastName",
          sortable: true,
        },
        {
          label: "Email",
          field: "email",
          sortable: true,
        },
      ] as Header[],
      tableOptions: {
        actions: new Array<Action>(),
        page: 0,
        searchable: true,
        sort: "id",
        sortOrder: "asc",
      } as Options,
      // predefined filter
      filter: "",
    };
  },
  methods: {
    /** load event to load data */
    loadPage(
      page: number,
      size: number,
      sort: string,
      ascending: boolean,
      search: string,
    ): Promise<{ rows: User[]; total: number }> {
      // encode query parameter
      search = encodeURIComponent(search);
      const filter = encodeURIComponent(this.filter);

      return accountService
        .getPage(page, size, sort, ascending, search, filter)
        .then((response: Page<User>) => ({
          rows: response.content,
          total: response.totalElements,
        }))
        .catch((error: any) => {
          AlertService.error(
            "Aktuell besteht keine Verbindung!",
            "Bitte versuchen Sie es in einiger Zeit erneut!",
            "Wenn das Problem weiterhin besteht, informieren Sie bitte den Helpdesk!",
            "Fehlermeldung: " + error,
          );
          throw error;
        });
    },
    onClick(user: User) {
      this.$router.push({ path: "/account/" + user.id });
    },
    newAccount() {
      this.$router.push({ name: "Register" });
    },
  },
});
</script>

<style lang="scss" scoped></style>

/**
 * This service wraps some buefy toast / snackbar message functions for a standardized usage through the whole project.
 */

/** import buefy toast component */
import { ToastProgrammatic as Toast } from "buefy";
import { NotificationProgrammatic as Notification } from "buefy";
import { SnackbarProgrammatic as Snackbar } from "buefy";

/** use outcommented code to use fontawesome icons */
// import { icon } from "@fortawesome/fontawesome-svg-core";

export function info(message: string): void {
  Snackbar.open({
    message: message,
    position: "is-bottom-left",
    type: "is-info",
    queue: false,
    duration: 3000,
  });
}

export function warning(...message: string[]): void {
  Toast.open({
    message: template("exclamation" /*faExclamation*/, message.flat()),
    position: "is-bottom",
    type: "is-warning",
    queue: false,
    duration: 4000,
  });
}

export function success(...message: string[]): void {
  Toast.open({
    message: template("check-circle" /*faCheckCircle*/, message.flat()),
    position: "is-bottom",
    type: "is-success",
    queue: false,
    duration: 3000,
  });
}

export function error(...message: string[]): void {
  Notification.open({
    message: template("alert-circle" /*faExclamationCircle*/, message.flat()),
    position: "is-bottom-right",
    hasIcon: true,
    type: "is-danger",
    duration: 8000,
    queue: false,
  });
}

function template(icon: any, message: string[]): string {
  message[0] = "<b>" + message[0] + "</b>";
  return `<div class="my-notification">
              <span class="icon">
              <i class="mdi mdi-24px mdi-${icon}"></i>
              <!-- <i> ${icon /*icon(icon).html*/}</i>-->
              </span>
              <span> 
                ${message.reduce((r, i) => (r ? r + "<br/>" + i : i))}
              </span>
            </div>`;
}

export default {
  error,
  success,
  warning,
  info,
};

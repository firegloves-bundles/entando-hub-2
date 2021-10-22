import { ToastNotification } from "carbon-components-react"
import "carbon-components/css/carbon-components.min.css"

const formatTime = (fn) => {
  return fn < 10 ? "0" + fn : "" + fn
}

/**
 * Renders a toast notification on the screen
 *
 * PROPS:
 * title (string): the notification's title
 * message (string): the notification's message
 * type (string): the type of the notification. must be one of the following: 'error', 'info', 'info-square', 'success', 'warning', 'warning-alt'
 * lowContrast (boolean): enables or disables the low contrast
 * style (object): additional custom style
 */
const Notification = ({ title, message, type, lowContrast, style }) => {
  const today = new Date()
  const hours = formatTime(today.getHours())
  const minutes = formatTime(today.getMinutes())
  const seconds = formatTime(today.getSeconds())

  const toastProps = {
    kind: type ? type : "info",
    title: title ? title : "",
    subtitle: message ? message : "",
    lowContrast: lowContrast ? lowContrast : true,
    caption: `${hours}:${minutes}:${seconds}`,
    timeout: 5000,
    style: style ? style : {},
  }

  return (
    <>
      <ToastNotification {...toastProps} />
    </>
  )
}

export default Notification

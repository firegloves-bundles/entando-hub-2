import { ToastNotification } from "carbon-components-react"
import { useEffect } from 'react'
import "carbon-components/css/carbon-components.min.css"

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

const Notification = ({ title, message, type, lowContrast, style, setShowNotification }) => {
  const now = new Date().toLocaleTimeString()

  const onCloseHandler = () => {
    setShowNotification(false)
  }

  const toastProps = {
    kind: type ? type : "info",
    title: title ? title : "",
    subtitle: message ? message : "",
    lowContrast: lowContrast ? lowContrast : true,
    caption: now,
    timeout: 10000,
    style: style ? style : {},
    onClose: onCloseHandler
  }

  return (
    <>
      <ToastNotification {...toastProps} />
    </>
  )
}

export default Notification

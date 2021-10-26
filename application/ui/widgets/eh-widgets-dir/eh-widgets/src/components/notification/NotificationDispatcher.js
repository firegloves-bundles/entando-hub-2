import React, { useState, useEffect } from "react"
import Notification from "./Notification"

/*
{ title, message, type, lowContrast, style }

<ToastNotification
  caption="00:00:00 AM"
  iconDescription="describes the close button"
  subtitle={<span>Subtitle text goes here. <a href="#example">Example link</a></span>}
  timeout={0}
  title="Notification title"
/>
*/

const NotificationDispatcher = () => {
  const [notificationProps, setNotificationProps] = useState({})
  const [showNotification, setShowNotification] = useState(false)

  /** SUCCESS EVENT */
  const onSuccessEvent = (e) => {
    setShowNotification(true)
    console.log(e.detail)
    setNotificationProps({
      ...e.detail,
    })
  }

  useEffect(() => {
    window.addEventListener("success-event", onSuccessEvent)

    return () => {
      window.removeEventListener("success-event", onSuccessEvent)
    }
  }, [])

  /** FAIL EVENT */
  const onFailEvent = (e) => {
    setShowNotification(true)
    setNotificationProps({
      ...e.detail,
      type: "error"
    })
  }

  useEffect(() => {
    window.addEventListener("fail-event", onFailEvent)

    return () => {
      window.removeEventListener("fail-event", onFailEvent)
    }
  }, [])

  return (
    <div>
      {showNotification && <Notification {...notificationProps} setShowNotification={setShowNotification} />}
    </div>
  )
}

export default NotificationDispatcher

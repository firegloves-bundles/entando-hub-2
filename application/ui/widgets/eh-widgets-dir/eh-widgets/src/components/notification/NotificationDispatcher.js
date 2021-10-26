import React, { useState, useEffect } from "react"
import Notification from "./Notification"
import { SUCCESS, FAIL } from "../../helpers/eventDispatcher"

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
      type: "success",
    })
  }

  useEffect(() => {
    window.addEventListener(SUCCESS, onSuccessEvent)

    return () => {
      window.removeEventListener(SUCCESS, onSuccessEvent)
    }
  }, [])

  /** FAIL EVENT */
  const onFailEvent = (e) => {
    setShowNotification(true)
    setNotificationProps({
      ...e.detail,
      type: "error",
    })
  }

  useEffect(() => {
    window.addEventListener(FAIL, onFailEvent)

    return () => {
      window.removeEventListener(FAIL, onFailEvent)
    }
  }, [])

  return (
    <div>
      {showNotification && (
        <Notification
          {...notificationProps}
          setShowNotification={setShowNotification}
        />
      )}
    </div>
  )
}

export default NotificationDispatcher

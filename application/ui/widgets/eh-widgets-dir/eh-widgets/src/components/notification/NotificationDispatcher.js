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
  // const [showNotification, setShowNotification] = useState(false)

  const onDeleteOrganisation = (e) => {
    // setShowNotification(true)
    console.log(e.detail)
    setNotificationProps({
      ...e.detail,
    })
    // setTimeout(() => setShowNotification(false), 5000)
    // setShowNotification(false)
  }

  console.log("NP", notificationProps)

  useEffect(() => {
    window.addEventListener("delete-organisation-ok", onDeleteOrganisation)

    return () => {
      window.removeEventListener("delete-organisation-ok", onDeleteOrganisation)
    }
  }, [])

  return (
    <div className="NOTIFICATION">
      <Notification {...notificationProps} />
    </div>
  )
}

export default NotificationDispatcher

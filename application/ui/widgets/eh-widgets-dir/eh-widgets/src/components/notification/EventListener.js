import React, { useState, useEffect } from "react"
import Notification from "./Notification"

/*
<ToastNotification
        caption="00:00:00 AM"
        iconDescription="describes the close button"
        subtitle={<span>Subtitle text goes here. <a href="#example">Example link</a></span>}
        timeout={0}
        title="Notification title"
      />
*/

const EventListener = () => {
  const [notificationProps, setNotificationProps] = useState({})

  const onDeleteOrganisation = (e) => {
    console.log(e.detail)
    setNotificationProps({
      ...e.detail
    })
  }

  useEffect(() => {
    window.addEventListener("delete-organisation-ok", onDeleteOrganisation)

    return () => {
      window.removeEventListener("delete-organisation-ok", onDeleteOrganisation)
    }
  }, [])

  return (
    <div>
      <Notification {...notificationProps}/>
    </div>
  )
}

export default EventListener

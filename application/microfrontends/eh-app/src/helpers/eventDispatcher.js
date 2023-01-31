export const fireEvent = (eventName, eventMessage) => {
  const customEvent = new CustomEvent(eventName, {
    detail: {
      message: eventMessage,
    },
  })
  window.dispatchEvent(customEvent)
}

export const SUCCESS = "success-event"

export const FAIL = "fail-event"

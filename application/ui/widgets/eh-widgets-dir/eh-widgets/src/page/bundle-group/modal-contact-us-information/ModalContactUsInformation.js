import {Button, Modal} from "carbon-components-react"
import ReactDOM from "react-dom"
import {useState} from "react"
import "./modal-contact-us-information.scss"
import i18n from "../../../i18n"

const ContactUsInfo = ({bundleGroup, children}) => {
  return (
      <div className="Modal-contact-us-code">

          <iframe title="Contact Us Form Modal" src={bundleGroup.contactUrl} style={{width:'100%',height:'400px'}}/>

      </div>
  )
}


export const ModalContactUsInformation = (props) => {
    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false)
        const [bundleGroup] = useState(props.bundleGroup)
        const [children] = useState(props.children)

        const onRequestClose = (e) => {
            setOpen(false)
        }

        const onRequestOpen = (e) => {
            setOpen(true)
        }

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent open={open} onRequestClose={onRequestClose} bundleGroup={bundleGroup} children={children}/>,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen}/>}
            </>
        )
    }


    return (
      <ModalStateManager
          renderLauncher={({onRequestOpen}) => (
              <Button onClick={onRequestOpen}>{i18n.t('component.button.contactUs')}</Button>
          )}>
        {({open, onRequestClose, bundleGroup, children}) => (
            <Modal
                className="Modal-contact-us-information"
                // modalLabel={"How to CONTACT US "}
                passiveModal
                open={open}
                onRequestClose={onRequestClose}>
              <ContactUsInfo bundleGroup={bundleGroup} children={children}/>
            </Modal>
        )}
      </ModalStateManager>
  )
}

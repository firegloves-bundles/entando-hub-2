import {Button, CodeSnippet, Modal} from "carbon-components-react"
import ReactDOM from "react-dom"
import {useState} from "react"
import "./modal-install-information.scss"

const InstallationInfo = ({bundleGroup, children}) => {
    const elemList = children.map((bundle, index)=>(
        <CodeSnippet key={index.toString()} type="multi" feedback="Copied to clipboard">
            {`ent bundler from-git -r ${bundle.gitRepoAddress} -d | ent kubectl apply -n entando -f -`}
        </CodeSnippet>
    ))

  return (
      <div className="Modal-install-code">
        Instruction for {bundleGroup.name}
        <div className="Modal-install-code">
          {elemList}
        </div>
      </div>
  )
}


export const ModalInstallInformation = (props) => {
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
              <Button onClick={onRequestOpen}>Install</Button>
          )}>
        {({open, onRequestClose, bundleGroup, children}) => (
            <Modal
                className="Modal-install-information"
                modalLabel={"How to install " + bundleGroup.name}
                passiveModal
                open={open}
                onRequestClose={onRequestClose}>
              <InstallationInfo bundleGroup={bundleGroup} children={children}/>
            </Modal>
        )}
      </ModalStateManager>
  )
}

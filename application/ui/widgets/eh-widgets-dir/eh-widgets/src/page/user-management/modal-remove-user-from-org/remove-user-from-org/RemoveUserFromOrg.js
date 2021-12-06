import { Content } from "carbon-components-react";
import { TrashCan32 } from '@carbon/icons-react'
import { MODAL_LABELS } from "../../../../helpers/constants";

const RemoveUserFromOrg = () => {

    return (
        <>
            <Content>
                <div className="Modal-remove-user-from-org">
                    <div className="Modal-remove-user-from-org-wrapper">
                        <TrashCan32 />
                    </div>
                    <div>
                        {MODAL_LABELS.REMOVE_USER_FROM_ORG_MSG}
                    </div>
                </div>
            </Content>
        </>
    )
}

export default RemoveUserFromOrg

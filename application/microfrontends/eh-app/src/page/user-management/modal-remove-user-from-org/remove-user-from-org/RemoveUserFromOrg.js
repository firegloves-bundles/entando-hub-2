import { Content } from "carbon-components-react";
import { TrashCan32 } from '@carbon/icons-react'
import i18n from "../../../../i18n";

const RemoveUserFromOrg = () => {

    return (
        <>
            <Content>
                <div className="Modal-remove-user-from-org">
                    <div className="Modal-remove-user-from-org-wrapper">
                        <TrashCan32 />
                    </div>
                    <div>
                        {i18n.t('modalMsg.removeUserFromOrgMsg')}
                    </div>
                </div>
            </Content>
        </>
    )
}

export default RemoveUserFromOrg

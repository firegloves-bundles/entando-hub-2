import {getAllUsers, getPortalUserByUsername, getSingleOrganisation} from "./Integration"
import {getUserName, isHubUser} from "../helpers/helpers";

//portal user
//EHUB-39
export const getPortalUserDetails_old = async (username) => {
    const userList = (await getAllUsers()).userList
    if (userList && userList.length) {
        const filteredUserList = userList.filter(user => user.username === username)
        if (filteredUserList.length > 0) {
            const user = filteredUserList[0]
            if (user.organisationIds && user.organisationIds.length > 0) {
                const organisation = (await getSingleOrganisation(user.organisationIds[0])).organisation
                return {
                    ...user,
                    organisation
                }
            }
            return {
                ...user
            }
        }
    }
    return undefined
}


export const getPortalUserDetails = async (username) => {
    const user = (await getPortalUserByUsername(username)).portalUser;
    if (user) {
        return {
            ...user
        }
    }
    return undefined
}

//EHUB-39
export const getCurrentUserOrganisation_old = async ()=>{
    //TODO cache user info
    if (isHubUser()) {
        const username = await getUserName()
        const portalUserDetail = await getPortalUserDetails(username)
        if (portalUserDetail && portalUserDetail.organisation) {
            return portalUserDetail.organisation
        }
    }
    return undefined
}

export const getCurrentUserOrganisation = async () => {
    //TODO cache user info
    if (isHubUser()) {
        const username = await getUserName()
        const portalUserDetail = await getPortalUserDetails(username)
        if (portalUserDetail && portalUserDetail.organisations && portalUserDetail.organisations[0]) {
            return portalUserDetail.organisations[0];
        }
    }
    return undefined
}

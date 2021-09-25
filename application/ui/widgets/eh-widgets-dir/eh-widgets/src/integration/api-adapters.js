import {getAllKCUsers, getAllUsers, getSingleOrganisation} from "./Integration"

//portal user
export const getPortalUserDetails = async (username) => {
    const userList = (await getAllUsers()).userList
    const filteredUserList = userList.filter(user => user.username === username)
    if (filteredUserList.length>0) {
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
    return undefined
}

export const getAvailableKcUsers = async (username) => {
    const kcUsers = (await getAllKCUsers()).kcUsers
    const portalUserUsernames = (await getAllUsers()).userList.map(u=>u.username)
    console.log("portalUserUsernames",portalUserUsernames)

    const availableUsers = kcUsers.filter(kcUser=>!portalUserUsernames.includes(kcUser.username))
    console.log("availableUsers",availableUsers)



    return availableUsers
}

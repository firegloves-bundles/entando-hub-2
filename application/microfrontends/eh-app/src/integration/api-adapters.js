import { getPortalUser } from "./Integration";
import { getUserName, isHubUser } from "../helpers/helpers";

//portal user
export const getPortalUserDetails = async () => {
    const user = (await getPortalUser()).portalUser;
    if (user) {
        return {
            ...user
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

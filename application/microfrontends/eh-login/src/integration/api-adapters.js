import { getPortalUser } from "./Integration";

//portal user
export const getPortalUserDetails = async (apiUrl) => {
    const user = (await getPortalUser(apiUrl)).portalUser;
    if (user) {
        return {
            ...user
        }
    }
    return undefined
}

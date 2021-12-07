//Hub Roles
export const AUTHOR = 'eh-author'
export const MANAGER = 'eh-manager'
export const ADMIN = 'eh-admin'

//Bundle Status
export const BUNDLE_STATUS = {
    NOT_PUBLISHED: 'NOT_PUBLISHED',
    PUBLISHED: 'PUBLISHED',
    PUBLISH_REQ: 'PUBLISH_REQ',
    DELETE_REQ: 'DELETE_REQ'
}

// HTTP Status
export const HTTP_STATUS = {
    EXPECTATION_FAILED: '417'
}

// All Button Labels
export const BUTTON_LABELS = {
    DELETE: "Delete",
    EDIT: "Edit",
    CANCEL: "Cancel",
    REMOVE: "Remove",
}

// All Modal Labels
export const MODAL_LABELS = {
    DELETE_BUNDLE_MSG: "Are you sure you want to delete this bundle?",
    REMOVE_USER_FROM_ORG_MSG: "Are you sure you want to remove this User from the Organization?",
}

// All API Response Key
export const API_RESPONSE_KEY = {
    EDITED_BUNDLE_GROUP: 'editedBundleGroup',
    PORTAL_USER: 'portalUser'
}

// Constant String
export const DELETED_BUNDLE = 'deletedBundle';
export const GIT_REPO = 'gitRepo';

/**
 * Messages
 */
export const MESSAGES = {
    NOTIFY_GUEST_PORTAL_USER_MSG: 'Your account does not currently have access to the Hub. Please contact your Administrator to request access.',
    IMPOSSIBLE_TO_REMOVE_USERS_MSG: 'Impossible to remove the user',
    USER_REMOVED_FROM_ORG_MSG: 'User removed from the organisation'
}

// All dropdown options
export const DROPDOWN_OPTIONS = {
    EDIT: "Edit",
    REMOVE: "Remove",
}

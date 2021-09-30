# Entando Hub - Getting Started

## 1. Install and Configure Requestly

- Add [Requestly extension](https://chrome.google.com/webstore/detail/requestly-redirect-url-mo/mdnleldcmiljblolnjhpnblkcekpdkpa) to Chrome
- Open Requestly, click on the kebab button and choose **New Group**
- Create a new group named **entando-hub**
- Click on the **New Rule** button and then on **Redirect Request**
- Click on **Ungrouped** and choose **entando-hub** from the dropdown menu
- Fill up the empty fields as follows:
    - Rule Name: `entando-hub-0.1.0.js`
    - If request `URL` `Contains` `http://hubdev.okd-entando.org/entando-de-app/cmsresources/entando-hub-bundle/static/js/entando-hub-0.1.0.js`
    - Destination: `http://localhost:3000/static/js/entando-hub-0.1.0.js`
- Click on **Save Rule**

## 2. Install Disable Content-Security-Policy
- Add [Disable Content-Security-Policy](https://chrome.google.com/webstore/detail/disable-content-security/ieelmcmcagommplceebfedjlakkhpden) to Chrome

## 3. Clone GitHub Repository
- Clone the [GitHub repository](https://github.com/entando-ps/entando-hub) of the project
- Checkout to master branch

## 4. Set Up the Environment Variables
- Browse to the directory **application/ui/widgets/eh-widgets-dir/eh-widgets**
- If not already present, create a file named `.env` and copy the following code into it:
    ```
    SASS_PATH=./node_modules;./src
    REACT_APP_PUBLIC_API_URL=http://hubdev.okd-entando.org/entando-hub-api/api
    REACT_APP_PUBLIC_ASSETS_URL=http://localhost:3000
    ```

## 5. Set Up node_modules Folder
- Browse to the directory **application/ui/widgets/eh-widgets-dir/eh-widgets**
- Run ``npm i`` command
- Copy ``webpack.config.js`` and ``webpackDevServer.config.js`` and paste them into **application/ui/widgets/eh-widgets-dir/eh-widgets/node_modules/react-scripts/config**

## 6. Run the Project
- Browse to the directory **application/ui/widgets/eh-widgets-dir/eh-widgets**
- Run ``npm start`` command
- Once the compiling is complete, navigate to the following address: ``http://hubdev.okd-entando.org/entando-de-app/en/eh_onecolumn.page#/``
- Activate the **Requestly rule** created in 1. and the **Content-Security-Policy** extension installed in 2.
- Refresh the page

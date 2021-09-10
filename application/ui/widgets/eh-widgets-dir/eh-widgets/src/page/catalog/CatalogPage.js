import CatalogPageHeader from "./catalog-page-header/CatalogPageHeader";
import {Breadcrumb, BreadcrumbItem, Content} from "carbon-components-react";
import CatalogPageContent from "./catalog-page-content/CatalogPageContent";

/*
const categories = Array.from(Array(3).keys()).map(index => {
    return {name: "name" + index, categoryId: "" + index};
})
*/

/*
{
name	string
description	string
bundleGroups	[...]
categoryId	string
}
 */

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}
 */
/*
const allBundleGroups = Array.from(Array(10).keys()).map(index => {
    return {
        bundleGroupId: "" + index,
        name: "name" + index,
        description: "description" + index,
        categories: ["" + categories[Math.floor(Math.random() * categories.length)].id],
        image: "image" + index
    };
})
*/


const CatalogPage = () => {
    return (
        <>
            <CatalogPageHeader/>
            <Content>
                <div className="bx--grid bx--grid--full-width catalog-page">
                    <div className="bx--row">
                        <div className="bx--col-lg-16">
                            <Breadcrumb aria-label="Page navigation">
                                <BreadcrumbItem>
                                    <a href="/">Home</a>
                                </BreadcrumbItem>
                            </Breadcrumb>
                        </div>
                    </div>
                    <div className="bx--row">
                        <div className="bx--col-lg-4">
                            Categories
                        </div>
                        <div className="bx--col-lg-6">
                            Catalog
                        </div>
                        <div className="bx--col-lg-6">
                            Search
                        </div>
                    </div>
                    <div className="bx--row">
                        <CatalogPageContent/>
                    </div>
                </div>
            </Content>

        </>
    );
};

export default CatalogPage;

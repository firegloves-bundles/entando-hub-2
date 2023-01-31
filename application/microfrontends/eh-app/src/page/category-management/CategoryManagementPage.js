import React, {useEffect, useState} from "react"

import {
  Content,
  DataTable,
  DataTableSkeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableHeader,
  TableRow,
  TableToolbar,
  TableToolbarContent,
} from "carbon-components-react"
import CategoryManagementOverflowMenu from "./overflow-menu/CategoryManagementOverflowMenu"
import {ModalAddNewCategory} from "./modal-add-new-category/ModalAddNewCategory"
import {getAllCategories,} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb"
import "./category-managment-page.scss"
import i18n from "../../i18n"
import { SHOW_NAVBAR_ON_MOUNTED_PAGE } from "../../helpers/constants"

/*
[
  {
    "name": "Entando inc.",
    "description": "Entando inc.",
    "bundleGroups": [
      "49",
      "51",
      "163",
      "52",
      "61",
      "141",
      "46",
      "142",
      "63",
      "143",
      "144"
    ],
    "organisationId": "1"
  },
  {
    "name": "Solving Team",
    "description": "Solving Team s.r.l",
    "bundleGroups": [
      "50"
    ],
    "organisationId": "2"
  }
]
 */

const headers = [
  {
    key: "name",
    header: "Name",
  },
  {
    key: "description",
    header: "Description",
  },
  {
    key: "overflow",
    header: "",
  },
]

const CategoryManagementPage = ({apiUrl}) => {
  const [reloadToken, setReloadToken] = useState(
    new Date().getTime().toString()
  )
  const [categories, setCategories] = useState([])
  const [isLoading, setIsLoading] = useState(true);

  // fetches the users to show
  useEffect(() => {
    (async () => {
      setIsLoading(true)
      const categoryList = (await getAllCategories(apiUrl)).categoryList;
      if (categoryList === undefined) {
        setIsLoading(false)
      }
      setCategories(categoryList.map(category=>{
        return {
          id: category.categoryId,
          ...category
        }
      }))
      setIsLoading(false)
    })()
  }, [apiUrl, reloadToken])

  const onAfterSubmit = () => {
    setReloadToken(new Date().getTime().toString())
  }
  return (
    <>
      <Content className="OrganizationManagmentPage">
        <div className="OrganizationManagmentPage-wrapper">
          <div className="bx--row">
            <div className="bx--col-lg-16 OrganizationManagmentPage-breadcrumb">
              <EhBreadcrumb
                pathElements={[{
                  path: i18n.t('navLink.categoryManagement'),
                  page: SHOW_NAVBAR_ON_MOUNTED_PAGE.isCategoryManagementPage
                }]}
              />
            </div>
          </div>
          <div className="bx--row">
            <div className="bx--col-lg-16 OrganizationManagmentPage-section">
              {isLoading && <DataTableSkeleton columnCount={3} rowCount={4}/>}
              {!isLoading && (<DataTable rows={categories} headers={headers}>
                {({
                  rows,
                  headers,
                  getTableProps,
                  getHeaderProps,
                  getRowProps,
                }) => (
                  <TableContainer title={i18n.t('page.management.categoryManagement')}>
                    <TableToolbar>
                      <TableToolbarContent>
                        <ModalAddNewCategory onAfterSubmit={onAfterSubmit} apiUrl={apiUrl}/>
                      </TableToolbarContent>
                    </TableToolbar>
                    <Table {...getTableProps()}>
                      <TableHead>
                        <TableRow>
                          {headers.map((header) => (
                            <TableHeader {...getHeaderProps({ header })}>
                              {header.header ? i18n.t(`component.bundleModalFields.${header.header.toLowerCase()}`) : ''}
                            </TableHeader>
                          ))}
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {rows.map((row) => (
                          <TableRow {...getRowProps({ row })}>
                            {row.cells.map((cell, index) => {
                              if (cell.id !== row.id + ":overflow") {
                                return (
                                  <TableCell key={cell.id}>
                                    {cell.value}
                                  </TableCell>
                                )
                              }
                              return (
                                <TableCell key={cell.id}>
                                  <CategoryManagementOverflowMenu
                                      apiUrl={apiUrl}
                                    categoryObj={{
                                      categoryId: row.id,
                                      name: row.cells[0].value,
                                      description: row.cells[1].value
                                    }}
                                    categories={categories}
                                    onAfterSubmit={onAfterSubmit}
                                    setReloadToken={setReloadToken}
                                  />
                                </TableCell>
                              )
                            })}
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                )}
              </DataTable>)}
            </div>
          </div>
        </div>
      </Content>
    </>
  )
}

export default CategoryManagementPage

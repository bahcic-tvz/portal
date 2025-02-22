import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { ICrudGetAllAction, TextFormat, getSortState, IPaginationBaseState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './article.reducer';
import { IArticle } from 'app/shared/model/article.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';

export interface IArticleProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const Article = (props: IArticleProps) => {
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE), props.location.search)
  );

  const getAllEntities = () => {
    props.getEntities(paginationState.activePage - 1, paginationState.itemsPerPage, `${paginationState.sort},${paginationState.order}`);
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (props.location.search !== endURL) {
      props.history.push(`${props.location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(props.location.search);
    const page = params.get('page');
    const sort = params.get('sort');
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [props.location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === 'asc' ? 'desc' : 'asc',
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const { articleList, match, loading, totalItems } = props;
  return (
    <div>
      <h2 id="article-heading">
        Articles
        <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
          <FontAwesomeIcon icon="plus" />
          &nbsp; Create new Article
        </Link>
      </h2>
      <div className="table-responsive">
        {articleList && articleList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('category')}>
                  Category <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('posted')}>
                  Posted <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('title')}>
                  Title <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('tag')}>
                  Tag <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('summary')}>
                  Summary <FontAwesomeIcon icon="sort" />
                </th>
                {/*<th className="hand" onClick={sort('photoURL')}>*/}
                {/*  Photo URL <FontAwesomeIcon icon="sort" />*/}
                {/*</th>*/}
                {/*<th className="hand" onClick={sort('photoAuthor')}>*/}
                {/*  Photo Author <FontAwesomeIcon icon="sort" />*/}
                {/*</th>*/}
                {/*<th className="hand" onClick={sort('poweredBy')}>*/}
                {/*  Powered By <FontAwesomeIcon icon="sort" />*/}
                {/*</th>*/}
                {/*<th className="hand" onClick={sort('poweredByURL')}>*/}
                {/*  Powered By URL <FontAwesomeIcon icon="sort" />*/}
                {/*</th>*/}
                {/*<th className="hand" onClick={sort('content')}>*/}
                {/*  Content <FontAwesomeIcon icon="sort" />*/}
                {/*</th>*/}
                <th className="hand" onClick={sort('isHero')}>
                  Is Hero <FontAwesomeIcon icon="sort" />
                </th>
                {/*<th>*/}
                {/*  Author <FontAwesomeIcon icon="sort" />*/}
                {/*</th>*/}
                <th />
              </tr>
            </thead>
            <tbody>
              {articleList.map((article, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${article.id}`} color="link" size="sm">
                      {article.id}
                    </Button>
                  </td>
                  <td>{article.category}</td>
                  <td>{article.posted ? <TextFormat type="date" value={article.posted} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{article.title}</td>
                  <td>{article.tag}</td>
                  <td>{article.summary}</td>
                  {/*<td>{article.photoURL}</td>*/}
                  {/*<td>{article.photoAuthor}</td>*/}
                  {/*<td>{article.poweredBy}</td>*/}
                  {/*<td>{article.poweredByURL}</td>*/}
                  {/*<td>{article.content}</td>*/}
                  <td>{article.hero ? 'true' : 'false'}</td>
                  {/*<td>{article.author ? article.author.id : ''}</td>*/}
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      {/*<Button tag={Link} to={`${match.url}/${article.id}`} color="info" size="sm">*/}
                      {/*  <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>*/}
                      {/*</Button>*/}
                      <Button
                        tag={Link}
                        to={`${match.url}/${article.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${article.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="danger"
                        size="sm"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Articles found</div>
        )}
      </div>
      {props.totalItems ? (
        <div className={articleList && articleList.length > 0 ? '' : 'd-none'}>
          <Row className="justify-content-center">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </Row>
          <Row className="justify-content-center">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={props.totalItems}
            />
          </Row>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

const mapStateToProps = ({ article }: IRootState) => ({
  articleList: article.entities,
  loading: article.loading,
  totalItems: article.totalItems,
});

const mapDispatchToProps = {
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Article);

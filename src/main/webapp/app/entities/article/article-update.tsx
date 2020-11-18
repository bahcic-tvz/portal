import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './article.reducer';
import { IArticle } from 'app/shared/model/article.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IArticleUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ArticleUpdate = (props: IArticleUpdateProps) => {
  const [authorId, setAuthorId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { articleEntity, users, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/article' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getUsers();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.posted = convertDateTimeToServer(values.posted);

    if (errors.length === 0) {
      const entity = {
        ...articleEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="portalApp.article.home.createOrEditLabel">Create or edit a Article</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : articleEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="article-id">ID</Label>
                  <AvInput id="article-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="categoryLabel" for="article-category">
                  Category
                </Label>
                <AvInput
                  id="article-category"
                  type="select"
                  className="form-control"
                  name="category"
                  value={(!isNew && articleEntity.category) || 'NEWS'}
                >
                  <option value="NEWS">NEWS</option>
                  <option value="SHOW">SHOW</option>
                  <option value="SPORT">SPORT</option>
                  <option value="LIFESTYLE">LIFESTYLE</option>
                  <option value="TECH">TECH</option>
                  <option value="VIRAL">VIRAL</option>
                  <option value="VIDEO">VIDEO</option>
                  <option value="SPONSORED">SPONSORED</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="postedLabel" for="article-posted">
                  Posted
                </Label>
                <AvInput
                  id="article-posted"
                  type="datetime-local"
                  className="form-control"
                  name="posted"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.articleEntity.posted)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="titleLabel" for="article-title">
                  Title
                </Label>
                <AvField id="article-title" type="text" name="title" />
              </AvGroup>
              <AvGroup>
                <Label id="tagLabel" for="article-tag">
                  Tag
                </Label>
                <AvField id="article-tag" type="text" name="tag" />
              </AvGroup>
              <AvGroup>
                <Label id="summaryLabel" for="article-summary">
                  Summary
                </Label>
                <AvField id="article-summary" type="text" name="summary" />
              </AvGroup>
              <AvGroup>
                <Label id="photoURLLabel" for="article-photoURL">
                  Photo URL
                </Label>
                <AvField id="article-photoURL" type="text" name="photoURL" />
              </AvGroup>
              <AvGroup>
                <Label id="photoAuthorLabel" for="article-photoAuthor">
                  Photo Author
                </Label>
                <AvField id="article-photoAuthor" type="text" name="photoAuthor" />
              </AvGroup>
              <AvGroup>
                <Label id="poweredByLabel" for="article-poweredBy">
                  Powered By
                </Label>
                <AvField id="article-poweredBy" type="text" name="poweredBy" />
              </AvGroup>
              <AvGroup>
                <Label id="poweredByURLLabel" for="article-poweredByURL">
                  Powered By URL
                </Label>
                <AvField id="article-poweredByURL" type="text" name="poweredByURL" />
              </AvGroup>
              <AvGroup>
                <Label id="contentLabel" for="article-content">
                  Content (will render HTML)
                </Label>
                <AvInput id="article-content" type="textarea" name="content" style={{height: '350px'}} />
              </AvGroup>
              <AvGroup check>
                <Label id="isHeroLabel">
                  <AvInput id="article-isHero" type="checkbox" className="form-check-input" name="isHero" />
                  Is Hero
                </Label>
              </AvGroup>
              <AvGroup>
                <Label for="article-author">Author</Label>
                <AvInput id="article-author" type="select" className="form-control" name="author.id">
                  <option value="" key="0" />
                  {users
                    ? users.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/article" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  articleEntity: storeState.article.entity,
  loading: storeState.article.loading,
  updating: storeState.article.updating,
  updateSuccess: storeState.article.updateSuccess,
});

const mapDispatchToProps = {
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ArticleUpdate);

import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './article.reducer';
import { IArticle } from 'app/shared/model/article.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IArticleDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ArticleDetail = (props: IArticleDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { articleEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Article [<b>{articleEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="category">Category</span>
          </dt>
          <dd>{articleEntity.category}</dd>
          <dt>
            <span id="posted">Posted</span>
          </dt>
          <dd>{articleEntity.posted ? <TextFormat value={articleEntity.posted} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{articleEntity.title}</dd>
          <dt>
            <span id="tag">Tag</span>
          </dt>
          <dd>{articleEntity.tag}</dd>
          <dt>
            <span id="summary">Summary</span>
          </dt>
          <dd>{articleEntity.summary}</dd>
          <dt>
            <span id="photoURL">Photo URL</span>
          </dt>
          <dd>{articleEntity.photoURL}</dd>
          <dt>
            <span id="photoAuthor">Photo Author</span>
          </dt>
          <dd>{articleEntity.photoAuthor}</dd>
          <dt>
            <span id="poweredBy">Powered By</span>
          </dt>
          <dd>{articleEntity.poweredBy}</dd>
          <dt>
            <span id="poweredByURL">Powered By URL</span>
          </dt>
          <dd>{articleEntity.poweredByURL}</dd>
          <dt>
            <span id="content">Content</span>
          </dt>
          <dd>{articleEntity.content}</dd>
          <dt>
            <span id="isHero">Is Hero</span>
          </dt>
          <dd>{articleEntity.isHero ? 'true' : 'false'}</dd>
          <dt>Author</dt>
          <dd>{articleEntity.author ? articleEntity.author.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/article" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/article/${articleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ article }: IRootState) => ({
  articleEntity: article.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ArticleDetail);

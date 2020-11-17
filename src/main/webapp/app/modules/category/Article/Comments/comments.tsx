import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Row, Col, Button } from 'reactstrap';
import { AvForm, AvInput } from 'availity-reactstrap-validation';
import {getCommentsForArticle, postComment} from "app/entities/comment/comment.reducer";

interface CommentsProp extends StateProps, DispatchProps {
  articleId: number
}

const Comments = (props: CommentsProp) => {

  useEffect(() => {
    props.getCommentsForArticle(props.articleId);
  }, [])

  const handleSubmit = (event, errors, { comment }) => {
    props.postComment({
      content: comment,
      articleId: props.articleId
    } as any)
  };

  const handleClose = (event, errors, { username, password, rememberMe }) => {};

  return (
    <div>
      <div>
        {props.comments.map(comment => (
          <div>
            {comment.content}
          </div>
        ))}
      </div>
      <AvForm onSubmit={handleSubmit}>
          <Row>
            <Col md="12">
              <AvInput
                name="comment"
                type="textarea"
                placeholder="Komentiraj..."
                required
                disabled={!props.isAuthenticated}
              />
            </Col>
          </Row>
          <div>
            {props.isAuthenticated ? null : 'Prijavite se da bi komentirali!'}
          </div>
          {/*<Button onClick={handleClose}>*/}
          {/*  Cancel*/}
          {/*</Button>{' '}*/}
          <Button
            color="primary"
            type="submit"
            disabled={!props.isAuthenticated}
          >
            Posalji komentar
          </Button>
      </AvForm>
    </div>
  )
};

const mapStateToProps = storeState => ({
  // account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  // loading: storeState.article.loading,
  comments: storeState.comment.entities
});

const mapDispatchToProps = {
  postComment,
  getCommentsForArticle,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Comments);

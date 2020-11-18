import './style.scss'
import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Row, Col, Button } from 'reactstrap';
import { AvForm, AvInput } from 'availity-reactstrap-validation';
import { postComment } from "app/entities/comment/comment.reducer";
import {IComment} from "app/shared/model/comment.model";
import {convertDateTimeForArticle, getDayCroatian} from "app/shared/util/date-utils";

let stringToColour = str => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  let colour = '#';
  for (let i = 0; i < 3; i++) {
    let value = (hash >> (i * 8)) & 0xFF;
    colour += ('00' + value.toString(16)).substr(-2);
  }
  return colour;
}

function invertHex(hex) {
  return (Number(`0x1${hex}`) ^ 0xFFFFFF).toString(16).substr(1).toUpperCase()
}

interface CommentsProp extends StateProps, DispatchProps {
  articleId: number
  comments: []
}

const Comments = (props: CommentsProp) => {
  const [commentValue, setCommentValue] = useState('')
  const commentInvalid = commentValue !== null && commentValue.length >= 999

  const handleSubmit = (event, errors, { comment }) => {
    if(commentValue.length <= 0) return
    props.postComment({
      content: comment,
      articleId: props.articleId
    } as any)
    setCommentValue('')
  };

  const getInitials = (comment: IComment) => {
    const first = comment.author.firstName?.substr(0, 1)
    const last = comment.author.lastName?.substr(0, 1)
    if(first && last) return (first + last).toUpperCase()
    return comment.author?.login.substr(0, 2).toUpperCase()
  }

  const getName = (comment: IComment) => {
    const first = comment.author.firstName
    const last = comment.author.lastName
    if(first && last) return (first + ' ' + last)
    return comment.author?.login
  }

  return (
    <div>
      <div>
        <div className={"comments-header-wrapper"}>
          <span className={"comments-header"} >
            <span>Komentari</span>{' '}
            <span className={"count"}>
              {props.comments.length}
            </span>
          </span>
        </div>
        {props.comments.map((comment: IComment) => (
          <div>
            <Row>
              <Col xs="1">
                <div
                  className={"fake-image"}
                  style={{
                    backgroundColor: stringToColour(comment.author.email),
                    color: invertHex(stringToColour(comment.author.email))
                  }}
                >
                  {getInitials(comment)}
                </div>
              </Col>
              <Col xs="11">
                <div className={"comment-cont-wrapper"}>
                  <div className={"full-name"}>
                    {getName(comment)}
                  </div>
                  <div className={"comment-date-time"}>
                  <span style={{textTransform: 'capitalize'}}>
                    {getDayCroatian(comment.posted)}
                  </span>{', '}
                    <span>{convertDateTimeForArticle(comment.posted)}</span>
                  </div>
                  <div className={"comment-content"}>
                    {comment.content}
                  </div>
                </div>
              </Col>
            </Row>
          </div>
        ))}
      </div>
      <AvForm
        onSubmit={handleSubmit}
        className={"comment-form"}
      >
        <Row>
          <Col md="10" sm="12">
            <AvInput
              name="comment"
              type="textarea"
              placeholder="Komentiraj..."
              disabled={!props.isAuthenticated}
              className={"textarea"}
              value={commentValue}
              onChange={e => setCommentValue(e.target.value)}
            />
            <div className={"log-in-comment"} style={{ color: 'red' }}>
              <b>
                {commentInvalid ? 'Maksimalna duljina komentara je 999 znakova!' : null}
              </b>
            </div>
            <div className={"log-in-comment"}>
              {props.isAuthenticated ? null : 'Prijavite se da bi komentirali!'}
            </div>
          </Col>
          <Col md="2" sm="12">
            <Button
              color="info"
              type="submit"
              disabled={!props.isAuthenticated || commentInvalid}
              className={"sub-button"}
            >
              <b>Pošalji</b>
            </Button>
          </Col>
        </Row>
      </AvForm>
    </div>
  )
};

const mapStateToProps = storeState => ({
  isAuthenticated: storeState.authentication.isAuthenticated,
});

const mapDispatchToProps = {
  postComment,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Comments);

package com.github.mrzhqiang.maplestory.domain;

import com.google.common.base.Objects;
import io.ebean.annotation.NotNull;

import javax.persistence.Embeddable;

@Embeddable
public class PKQuestion {

    @NotNull
    Integer questionSet;
    @NotNull
    Integer questionId;

    public Integer getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(Integer questionSet) {
        this.questionSet = questionSet;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PKQuestion that = (PKQuestion) o;
        return Objects.equal(questionSet, that.questionSet) &&
                Objects.equal(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(questionSet, questionId);
    }
}

package com.hackernews.api.model.ui;

import com.hackernews.api.model.client.CommentDetails;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetailsUi implements Serializable {
  private long commentId;
  private String authorId;
  private int authorActiveTime;
  private String commentText;

  /**
   * Transformation Function for Ui Model.
   *
   * @param commentDetails {@link CommentDetails}
   * @return Corresponding details to be shown on UI.
   */
  public static CommentDetailsUi from(CommentDetails commentDetails, UserDetails userDetails) {
    LocalDate timeCreated = Instant.ofEpochSecond(userDetails.getCreated())
        .atZone(ZoneId.systemDefault()).toLocalDate();
    return CommentDetailsUi.builder()
        .commentId(commentDetails.getId())
        .authorId(commentDetails.getBy())
        .authorActiveTime(Period.between(timeCreated, LocalDate.now()).getYears())
        .commentText(commentDetails.getText())
        .build();
  }
}
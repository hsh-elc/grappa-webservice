package proforma.util21;

import proforma.util.*;
import proforma.util.boundary.SubmissionBoundary;
import proforma.util.boundary.TaskBoundary;
import proforma.xml.AbstractProformaType;
import proforma.xml.AbstractSubmissionType;
import proforma.xml21.FeedbackLevelType;
import proforma.xml21.LmsType;
import proforma.xml21.ResultSpecType;
import proforma.xml21.SubmissionType;

public class Proforma21SubmissionHelper extends ProformaSubmissionHelper {

    public Proforma21SubmissionHelper(ProformaVersion pv) {
        super(pv);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractProformaType> Class<T> getPojoType() {
        return (Class<T>) SubmissionType.class;
    }


    @Override
    public ProformaSubmissionTaskHandle getSubmissionTaskHandle(SubmissionLive submission, TaskBoundary tb) {
        return new Proforma21SubmissionTaskHandle(submission, tb);
    }

    @Override
    public ProformaSubmissionSubmissionHandle getSubmissionSubmissionHandle(SubmissionLive submission,
                                                                            SubmissionBoundary sb) {
        return new Proforma21SubmissionSubmissionHandle(submission, sb);
    }

    @Override
    public ProformaSubmissionRestrictionsChecker getSubmissionRestrictionsChecker(SubmissionLive submission, TaskBoundary tb) {
        return new Proforma21SubmissionRestrictionsChecker(submission, tb);
    }


    @Override
    public String getSubmissionId(AbstractSubmissionType submission) {
        SubmissionType s = (SubmissionType) submission;
        return s.getId();
    }


    @Override
    public void setResultSpecDetailsIfEmpty(AbstractSubmissionType submission, String structure, String format,
                                            String studentFeedbackLevel, String teacherFeedbackLevel) {
        SubmissionType s = (SubmissionType) submission;
        ResultSpecType rs = s.getResultSpec();
        if (rs.getStructure() == null || rs.getStructure().isEmpty()) {
            rs.setStructure(structure);
        }
        if (rs.getStudentFeedbackLevel() == null) {
            rs.setStudentFeedbackLevel(FeedbackLevelType.fromValue(studentFeedbackLevel));
        }
        if (rs.getTeacherFeedbackLevel() == null) {
            rs.setTeacherFeedbackLevel(FeedbackLevelType.fromValue(teacherFeedbackLevel));
        }
        if (rs.getFormat() == null || rs.getFormat().isEmpty()) {
            rs.setFormat(format);
        }
    }


    public void addObjectsToLmsAnyNamespace(AbstractSubmissionType submission, Object... objects) {
        SubmissionType s = (SubmissionType) submission;
        LmsType lms = s.getLms();
        if (lms == null) {
            lms = new LmsType();
            s.setLms(lms);
        }
        for (Object o : objects) {
            s.getLms().getAny().add(o);
        }
    }

    public String[] getAllFeedbackLevels() {
        String[] result = new String[FeedbackLevelType.values().length];
        for (int i = 0; i < result.length; i++) {
            result[i] = FeedbackLevelType.values()[i].value();
        }
        return result;
    }

    @Override
    public String getResultSpecFormat(AbstractSubmissionType submission) {
        SubmissionType st = (SubmissionType) submission;
        if (st == null) return null;
        ResultSpecType rs = st.getResultSpec();
        if (rs == null) return null;
        return rs.getFormat();
    }

}

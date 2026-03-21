package dvhlong.be.common.constant;

public final class AppConstant {
	private AppConstant() {}

	public static final String LOCALE_VIETNAMESE = "vi";

	public static final String OTP_ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final String OTP_PREFIX = "otp:";
	public static final long OTP_EXPIRY_MINUTES = 1;
	public static final int OTP_LENGTH = 6;
	public static final int PASSWORD_MIN_LENGTH = 8;

	public static final String MESSAGE_RESPONSE_FIELD_KEY = "field";
	public static final String MESSAGE_RESPONSE_CODE_KEY = "code";
	public static final String MESSAGE_RESPONSE_PARAMS_KEY = "params";

}

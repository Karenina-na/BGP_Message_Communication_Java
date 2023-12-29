package org.example.message.notification;

public enum BGPNotificationSubErrorCode {

        // Message Header Error
        ConnectionNotSync(1),
        BadMessageLength(2),
        BadMessageType(3),

        // OPEN Message Error
        UnsupportedVersionNumber(1),
        BadPeerAS(2),
        BadBGPIdentifier(3),
        UnsupportedOptionalParameter(4),
        UnacceptableHoldTime(6),

        // UPDATE Message Error
        MalformedAttributeList(1),
        UnrecognizedWellKnownAttribute(2),
        MissingWellKnownAttribute(3),
        AttributeFlagsError(4),
        AttributeLengthError(5),
        InvalidOriginAttribute(6),
        InvalidNextHopAttribute(8),
        OptionalAttributeError(9),
        InvalidNetworkField(10),
        MalformedASPath(11),

        // FSM Error
        UnspecifiedError(0),
        ReceiveUnexpectedMessageInOpenSentState(1),
        ReceiveUnexpectedMessageInOpenConfirmState(2),
        ReceiveUnexpectedMessageInEstablishedState(3),

        // Cease
        MaximumNumberOfPrefixReached(1),
        AdministrativeShutdown(2),
        PeerDeconfigured(3),
        AdministrativeReset(4),
        ConnectionRejected(5),
        OtherConfigurationChange(6),
        ConnectionCollisionResolution(7),
        OutOfResources(8);

        private final int value;

        BGPNotificationSubErrorCode(int value) {
            this.value = value;
        }

        // 根据值创建枚举类
        public static BGPNotificationSubErrorCode fromValue(BGPNotificationErrorCode major, int value){
            switch (major) {
                case MessageHeaderError:
                    switch (value) {
                        case 1:
                            return ConnectionNotSync;
                        case 2:
                            return BadMessageLength;
                        case 3:
                            return BadMessageType;
                        default:
                            throw new IllegalArgumentException(String.valueOf(value));
                    }
                case OPENMessageError:
                    switch (value) {
                        case 1:
                            return UnsupportedVersionNumber;
                        case 2:
                            return BadPeerAS;
                        case 3:
                            return BadBGPIdentifier;
                        case 4:
                            return UnsupportedOptionalParameter;
                        case 6:
                            return UnacceptableHoldTime;
                        default:
                            throw new IllegalArgumentException(String.valueOf(value));
                    }
                case UPDATEMessageError:
                    switch (value) {
                        case 1:
                            return MalformedAttributeList;
                        case 2:
                            return UnrecognizedWellKnownAttribute;
                        case 3:
                            return MissingWellKnownAttribute;
                        case 4:
                            return AttributeFlagsError;
                        case 5:
                            return AttributeLengthError;
                        case 6:
                            return InvalidOriginAttribute;
                        case 8:
                            return InvalidNextHopAttribute;
                        case 9:
                            return OptionalAttributeError;
                        case 10:
                            return InvalidNetworkField;
                        case 11:
                            return MalformedASPath;
                        default:
                            throw new IllegalArgumentException(String.valueOf(value));
                    }
                case FiniteStateMachineError:
                    switch (value) {
                        case 0:
                            return UnspecifiedError;
                        case 1:
                            return ReceiveUnexpectedMessageInOpenSentState;
                        case 2:
                            return ReceiveUnexpectedMessageInOpenConfirmState;
                        case 3:
                            return ReceiveUnexpectedMessageInEstablishedState;
                        default:
                            throw new IllegalArgumentException(String.valueOf(value));
                    }
                case Cease:
                    switch (value) {
                        case 1:
                            return MaximumNumberOfPrefixReached;
                        case 2:
                            return AdministrativeShutdown;
                        case 3:
                            return PeerDeconfigured;
                        case 4:
                            return AdministrativeReset;
                        case 5:
                            return ConnectionRejected;
                        case 6:
                            return OtherConfigurationChange;
                        case 7:
                            return ConnectionCollisionResolution;
                        case 8:
                            return OutOfResources;
                        default:
                            throw new IllegalArgumentException(String.valueOf(value));
                    }
                default:
                    throw new IllegalArgumentException(String.valueOf(value));
            }
        }

        public int getValue() {
            return value;
        }
}

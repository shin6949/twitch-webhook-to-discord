import { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { ToastState } from "../components/CustomToast";
import {getMessaging, getToken, Messaging, NotificationPermission} from "firebase/messaging";
import { useFirebaseApp } from "../context/FirebaseContext";

// Custom Component
import NotificationCard from "../components/push-manage/NotificationCard";

interface Notification {
    form_id: number;
    profile_image: string;
    nickname: string;
    login_id: string;
    notification_type: string;
    interval_minute: number;
}

export const getStaticProps = async ({ locale }: { locale: string }) => {
    return {
        props: {
            ...(await serverSideTranslations(locale, ["push-manage", "common"])),
        },
    };
};

const NotificationManagePage = () => {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [showToast, setShowToast] = useState<ToastState>({
        show: false,
        message: "",
        variant: "secondary",
    });
    const [messaging, setMessaging] = useState<Messaging | null>(null);
    const [token, setToken] = useState<string>("");
    const firebaseApp = useFirebaseApp();

    const requestNotificationPermission = async () => {
        try {
            const permission = await Notification.requestPermission();
            if (permission === NotificationPermission.granted) {
                console.log("Notification permission granted.");
            } else {
                console.log("Notification permission denied.");
            }
        } catch (error) {
            console.error("Failed to request notification permission:", error);
        }
    };

    const getFirebaseMessagingToken = async (messagingInstance: Messaging) => {
        try {
            const token = await getToken(messagingInstance);
            console.log("Firebase messaging token:", token);
            // Pass the token to NotificationCard
            setToken(token);
        } catch (error) {
            console.error("Failed to get Firebase messaging token:", error);
        }
    };

    const fetchNotifications = async (token: string) => {
        try {
            const response = await fetch(`/api/push-manage/get?token=${token}`);
            const data = await response.json();
            setNotifications(data);
        } catch (error) {
            console.error('Failed to fetch notifications:', error);
        }
    };

    useEffect(() => {
        if (typeof window !== 'undefined' && messaging === null) {
            if (firebaseApp) {
                const messagingInstance = getMessaging(firebaseApp);
                setMessaging(messagingInstance);
                requestNotificationPermission();
                getFirebaseMessagingToken(messagingInstance);
            }
        }
        if (token) {
            fetchNotifications(token);
        }
    }, [firebaseApp, token]);

    const handleDeleteNotification = async (formId: number, token: string) => {
        try {
            await fetch(`/api/notifications/${formId}`, {
                method: 'DELETE',
            });
            fetchNotifications(token);
        } catch (error) {
            console.error('Failed to delete notification:', error);
        }
    };

    return (
        <Container>
            <h1>Notifications</h1>
            {notifications.map((notification) => (
                <NotificationCard
                    key={notification.form_id}
                    formId={notification.form_id}
                    profileImage={notification.profile_image}
                    nickname={notification.nickname}
                    loginId={notification.login_id}
                    notificationType={notification.notification_type}
                    intervalMinute={notification.interval_minute}
                    token={token}
                    onClick={handleDeleteNotification}
                />
            ))}
        </Container>
    );
};

export default NotificationManagePage;